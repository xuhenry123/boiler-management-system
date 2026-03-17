package com.boiler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * PID控制算法服务类
 * 
 * 【算法总体说明】
 * 本类实现了PID（比例-积分-微分）控制算法，用于供热系统的温度、压力、流量等参数的自动调节。
 * 同时提供了模糊PID控制算法，能够根据系统状态自动调整PID参数，提高控制效果。
 * 
 * 【PID控制原理】
 * PID控制器是一种基于反馈的控制算法，其输出由三部分组成：
 * 1. 比例项（P）：与当前误差成正比，消除当前误差
 * 2. 积分项（I）：与误差的累积成正比，消除稳态误差
 * 3. 微分项（D）：与误差变化率成正比，预测未来误差趋势
 * 
 * 【控制公式】
 * output(t) = Kp * e(t) + Ki * ∫e(t)dt + Kd * de(t)/dt
 * 其中：
 *   e(t)：设定值与测量值的差值（误差）
 *   Kp：比例系数
 *   Ki：积分系数
 *   Kd：微分系数
 * 
 * @author 锅炉管理系统
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PIDControlService {

    /** 比例系数Kp，决定对当前误差的响应强度 */
    private double kp = 1.2;
    /** 积分系数Ki，决定对累积误差的响应强度 */
    private double ki = 0.3;
    /** 微分系数Kd，决定对误差变化率的响应强度 */
    private double kd = 0.1;
    /** 设定值（目标值），如目标温度 */
    private double setpoint = 50.0;
    /** 输出下限 */
    private double outputMin = 0.0;
    /** 输出上限 */
    private double outputMax = 100.0;
    
    /** 上一次误差值，用于计算微分项 */
    private double lastError = 0.0;
    /** 误差积分项，用于计算积分项 */
    private double integral = 0.0;
    /** 上一次计算时间，用于计算采样周期 */
    private long lastTime = System.currentTimeMillis();

    /**
     * 标准PID控制计算
     * 
     * 【功能说明】
     * 根据当前测量值和设定值，计算PID控制器的输出
     * 
     * 【算法流程】
     * 1. 计算采样周期dt
     * 2. 计算当前误差e = 设定值 - 测量值
     * 3. 计算误差积分项（带抗饱和处理）
     * 4. 计算误差微分项
     * 5. 计算PID输出：output = Kp*e + Ki*∫e + Kd*de/dt
     * 6. 输出限幅（抗饱和处理）
     * 
     * @param measuredValue 当前测量值（如当前温度）
     * @return 控制输出结果，包含设定值、测量值、误差、输出及PID参数
     */
    public Map<String, Object> calculate(double measuredValue) {
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        // 计算采样周期（秒）
        double dt = (currentTime - lastTime) / 1000.0;
        
        // 防止除零错误
        if (dt <= 0) {
            dt = 0.1;
        }

        // 计算误差：设定值 - 测量值
        double error = setpoint - measuredValue;
        
        // 积分项累加（误差 × 采样周期）
        integral += error * dt;
        
        // 积分项限幅（抗积分饱和）
        if (integral > outputMax) {
            integral = outputMax;
        } else if (integral < outputMin) {
            integral = outputMin;
        }
        
        // 计算微分项（误差变化率）
        double derivative = (error - lastError) / dt;
        
        // PID控制输出计算
        double output = kp * error + ki * integral + kd * derivative;
        
        // 输出限幅（抗饱和）
        if (output > outputMax) {
            output = outputMax;
        } else if (output < outputMin) {
            output = outputMin;
        }
        
        // 保存当前状态供下次计算使用
        lastError = error;
        lastTime = currentTime;

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("setpoint", BigDecimal.valueOf(setpoint).setScale(2, RoundingMode.HALF_UP));
        result.put("measuredValue", BigDecimal.valueOf(measuredValue).setScale(2, RoundingMode.HALF_UP));
        result.put("error", BigDecimal.valueOf(error).setScale(2, RoundingMode.HALF_UP));
        result.put("output", BigDecimal.valueOf(output).setScale(2, RoundingMode.HALF_UP));
        result.put("kp", BigDecimal.valueOf(kp).setScale(4, RoundingMode.HALF_UP));
        result.put("ki", BigDecimal.valueOf(ki).setScale(4, RoundingMode.HALF_UP));
        result.put("kd", BigDecimal.valueOf(kd).setScale(4, RoundingMode.HALF_UP));
        
        return result;
    }

    /**
     * 模糊PID控制计算
     * 
     * 【功能说明】
     * 模糊PID控制将模糊控制与PID控制相结合，根据系统当前状态动态调整PID参数。
     * 当误差较大时增大Kp以快速响应；当误差较小时增大Ki以消除稳态误差；
     * 当误差变化剧烈时增大Kd以抑制超调。
     * 
     * 【模糊规则】
     * 1. |e|较大时：增大Kp，加快响应速度
     * 2. |e|较小时：增大Ki，消除稳态误差
     * 3. |e|较小且|de/dt|较大时：增大Kd，抑制超调
     * 4. |e|较大时：减小Ki，避免积分饱和
     * 
     * @param measuredValue 当前测量值
     * @param error 当前误差
     * @param deltaError 误差变化率
     * @return 调整后的PID控制输出
     */
    public Map<String, Object> calculateFuzzyPID(double measuredValue, double error, double deltaError) {
        // 计算模糊调整系数
        double[] kpAdjust = calculateKpAdjustment(error, deltaError);
        double[] kiAdjust = calculateKiAdjustment(error);
        double[] kdAdjust = calculateKdAdjustment(error, deltaError);
        
        // 应用调整系数
        double adjustedKp = kp * kpAdjust[0];
        double adjustedKi = ki * kiAdjust[0];
        double adjustedKd = kd * kdAdjust[0];
        
        // 计算采样周期
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastTime) / 1000.0;
        
        if (dt <= 0) {
            dt = 0.1;
        }
        
        // 积分项累加
        integral += error * dt;
        
        // 积分项限幅
        if (integral > outputMax) {
            integral = outputMax;
        } else if (integral < outputMin) {
            integral = outputMin;
        }
        
        // 微分项（直接使用误差变化率）
        double derivative = deltaError;
        
        // 计算控制输出
        double output = adjustedKp * error + adjustedKi * integral + adjustedKd * derivative;
        
        // 输出限幅
        if (output > outputMax) {
            output = outputMax;
        } else if (output < outputMin) {
            output = outputMin;
        }
        
        // 保存状态
        lastError = error;
        lastTime = currentTime;

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("setpoint", BigDecimal.valueOf(setpoint).setScale(2, RoundingMode.HALF_UP));
        result.put("measuredValue", BigDecimal.valueOf(measuredValue).setScale(2, RoundingMode.HALF_UP));
        result.put("adjustedKp", BigDecimal.valueOf(adjustedKp).setScale(4, RoundingMode.HALF_UP));
        result.put("adjustedKi", BigDecimal.valueOf(adjustedKi).setScale(4, RoundingMode.HALF_UP));
        result.put("adjustedKd", BigDecimal.valueOf(adjustedKd).setScale(4, RoundingMode.HALF_UP));
        result.put("output", BigDecimal.valueOf(output).setScale(2, RoundingMode.HALF_UP));
        
        return result;
    }

    /**
     * 计算比例系数Kp的模糊调整
     * 
     * 【模糊规则】
     * - |e| > 30（大误差）：增大Kp，快速消除误差
     * - |e| > 15（中误差）：适度增大Kp
     * - |e| < 5（小误差）：减小Kp，避免超调
     * - |de/dt| > 10（快速变化）：增大Kp，增强响应
     * 
     * @param error 当前误差
     * @param deltaError 误差变化率
     * @return Kp调整系数数组
     */
    private double[] calculateKpAdjustment(double error, double deltaError) {
        double adjustment = 1.0;
        
        // 根据误差大小调整
        if (Math.abs(error) > 30) {
            adjustment = 1.2;  // 大误差时增大Kp
        } else if (Math.abs(error) > 15) {
            adjustment = 1.1;  // 中误差时适度增大
        } else if (Math.abs(error) < 5) {
            adjustment = 0.8;  // 小误差时减小
        }
        
        // 根据误差变化率调整
        if (Math.abs(deltaError) > 10) {
            adjustment *= 1.2;  // 快速变化时增强响应
        }
        
        return new double[]{adjustment};
    }

    /**
     * 计算积分系数Ki的模糊调整
     * 
     * 【模糊规则】
     * - |e| < 5（小误差）：增大Ki，消除稳态误差
     * - |e| > 20（大误差）：减小Ki，避免积分饱和
     * 
     * @param error 当前误差
     * @return Ki调整系数数组
     */
    private double[] calculateKiAdjustment(double error) {
        double adjustment = 1.0;
        
        if (Math.abs(error) < 5) {
            adjustment = 1.3;  // 小误差时增大积分作用
        } else if (Math.abs(error) > 20) {
            adjustment = 0.5;  // 大误差时减小积分作用
        }
        
        return new double[]{adjustment};
    }

    /**
     * 计算微分系数Kd的模糊调整
     * 
     * 【模糊规则】
     * - |e| < 5 且 |de/dt| > 5：增大Kd，抑制超调
     * - |e| > 20（大误差）：减小Kd，避免微分饱和
     * 
     * @param error 当前误差
     * @param deltaError 误差变化率
     * @return Kd调整系数数组
     */
    private double[] calculateKdAdjustment(double error, double deltaError) {
        double adjustment = 1.0;
        
        if (Math.abs(error) < 5 && Math.abs(deltaError) > 5) {
            adjustment = 1.5;  // 快速接近目标时增强微分作用
        } else if (Math.abs(error) > 20) {
            adjustment = 0.7;  // 大误差时减弱微分作用
        }
        
        return new double[]{adjustment};
    }

    /**
     * 设置PID参数
     * 
     * @param kp 比例系数
     * @param ki 积分系数
     * @param kd 微分系数
     */
    public void setParameters(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    /**
     * 设置设定值
     * 
     * @param setpoint 目标设定值
     */
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    /**
     * 设置输出限幅范围
     * 
     * @param min 输出下限
     * @param max 输出上限
     */
    public void setOutputLimits(double min, double max) {
        this.outputMin = min;
        this.outputMax = max;
    }

    /**
     * 重置PID控制器
     * 清除积分项和误差历史，用于切换工况或重新启动控制
     */
    public void reset() {
        lastError = 0.0;
        integral = 0.0;
        lastTime = System.currentTimeMillis();
    }
}
