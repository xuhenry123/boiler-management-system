package com.boiler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PIDControlService {

    private double kp = 1.2;
    private double ki = 0.3;
    private double kd = 0.1;
    private double setpoint = 50.0;
    private double outputMin = 0.0;
    private double outputMax = 100.0;
    
    private double lastError = 0.0;
    private double integral = 0.0;
    private long lastTime = System.currentTimeMillis();

    public Map<String, Object> calculate(double measuredValue) {
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastTime) / 1000.0;
        
        if (dt <= 0) {
            dt = 0.1;
        }

        double error = setpoint - measuredValue;
        integral += error * dt;
        
        if (integral > outputMax) {
            integral = outputMax;
        } else if (integral < outputMin) {
            integral = outputMin;
        }
        
        double derivative = (error - lastError) / dt;
        
        double output = kp * error + ki * integral + kd * derivative;
        
        if (output > outputMax) {
            output = outputMax;
        } else if (output < outputMin) {
            output = outputMin;
        }
        
        lastError = error;
        lastTime = currentTime;

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

    public Map<String, Object> calculateFuzzyPID(double measuredValue, double error, double deltaError) {
        double[] kpAdjust = calculateKpAdjustment(error, deltaError);
        double[] kiAdjust = calculateKiAdjustment(error);
        double[] kdAdjust = calculateKdAdjustment(error, deltaError);
        
        double adjustedKp = kp * kpAdjust[0];
        double adjustedKi = ki * kiAdjust[0];
        double adjustedKd = kd * kdAdjust[0];
        
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastTime) / 1000.0;
        
        if (dt <= 0) {
            dt = 0.1;
        }
        
        integral += error * dt;
        
        if (integral > outputMax) {
            integral = outputMax;
        } else if (integral < outputMin) {
            integral = outputMin;
        }
        
        double derivative = deltaError;
        
        double output = adjustedKp * error + adjustedKi * integral + adjustedKd * derivative;
        
        if (output > outputMax) {
            output = outputMax;
        } else if (output < outputMin) {
            output = outputMin;
        }
        
        lastError = error;
        lastTime = currentTime;

        Map<String, Object> result = new HashMap<>();
        result.put("setpoint", BigDecimal.valueOf(setpoint).setScale(2, RoundingMode.HALF_UP));
        result.put("measuredValue", BigDecimal.valueOf(measuredValue).setScale(2, RoundingMode.HALF_UP));
        result.put("adjustedKp", BigDecimal.valueOf(adjustedKp).setScale(4, RoundingMode.HALF_UP));
        result.put("adjustedKi", BigDecimal.valueOf(adjustedKi).setScale(4, RoundingMode.HALF_UP));
        result.put("adjustedKd", BigDecimal.valueOf(adjustedKd).setScale(4, RoundingMode.HALF_UP));
        result.put("output", BigDecimal.valueOf(output).setScale(2, RoundingMode.HALF_UP));
        
        return result;
    }

    private double[] calculateKpAdjustment(double error, double deltaError) {
        double adjustment = 1.0;
        
        if (Math.abs(error) > 30) {
            adjustment = 1.2;
        } else if (Math.abs(error) > 15) {
            adjustment = 1.1;
        } else if (Math.abs(error) < 5) {
            adjustment = 0.8;
        }
        
        if (Math.abs(deltaError) > 10) {
            adjustment *= 1.2;
        }
        
        return new double[]{adjustment};
    }

    private double[] calculateKiAdjustment(double error) {
        double adjustment = 1.0;
        
        if (Math.abs(error) < 5) {
            adjustment = 1.3;
        } else if (Math.abs(error) > 20) {
            adjustment = 0.5;
        }
        
        return new double[]{adjustment};
    }

    private double[] calculateKdAdjustment(double error, double deltaError) {
        double adjustment = 1.0;
        
        if (Math.abs(error) < 5 && Math.abs(deltaError) > 5) {
            adjustment = 1.5;
        } else if (Math.abs(error) > 20) {
            adjustment = 0.7;
        }
        
        return new double[]{adjustment};
    }

    public void setParameters(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public void setOutputLimits(double min, double max) {
        this.outputMin = min;
        this.outputMax = max;
    }

    public void reset() {
        lastError = 0.0;
        integral = 0.0;
        lastTime = System.currentTimeMillis();
    }
}
