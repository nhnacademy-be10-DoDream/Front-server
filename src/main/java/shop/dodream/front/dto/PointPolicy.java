package shop.dodream.front.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
public class PointPolicy {
    private Long pointPolicyId;
    @NotNull
    private PolicyType policyType;
    @NotNull
    @Min(0)
    private BigDecimal rate;
    @NotNull
    @Min(0)
    private long basePoint;
    @NotNull
    private GradeType grade;

    public BigDecimal getDisplayRate() {
        return rate != null ?
                rate.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;
    }

    public PointPolicy withRateFromPercentage() {

        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Rate must be between 0 and 100, got: " + rate);
        }

        this.rate = rate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        return this;
    }
}
