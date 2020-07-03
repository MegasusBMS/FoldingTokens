package Main;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class TokenCalc 
{
	private final MathContext mathContext = new MathContext(32,RoundingMode.HALF_EVEN);
	
	public BigDecimal f(BigDecimal x,BigDecimal L, BigDecimal M, BigDecimal k, BigDecimal midPoint)
	{
		if (x.compareTo(midPoint) == -1)
		{
			x = midPoint.add(BigDecimal.ZERO);
		}
		
		BigDecimal alaDeSus = L.subtract(M);
		BigDecimal alaDeJos = BigDecimal.ONE.add(BigDecimal.valueOf(Math.E).pow(k.multiply(x.subtract(midPoint)).intValue()));
		return alaDeSus.divide(alaDeJos,mathContext).add(M);
	}
	public BigDecimal tokens(BigDecimal credits,BigDecimal wu)
	{
		BigDecimal ratio = credits.divide(wu,mathContext);
		BigDecimal fixedRatio = ratio.multiply(BigDecimal.valueOf(0.02d));
		
		if (fixedRatio.compareTo(BigDecimal.valueOf(10)) == -1)
		{
			fixedRatio = BigDecimal.valueOf(10);
		}
		
		if (fixedRatio.compareTo(BigDecimal.valueOf(100)) == 1)
		{
			fixedRatio = BigDecimal.valueOf(100);
		}
		
		return ratio.multiply(f(fixedRatio,BigDecimal.valueOf(0.01),BigDecimal.valueOf(0.001),BigDecimal.valueOf(0.2),BigDecimal.valueOf(10+100).divide(BigDecimal.valueOf(6),mathContext)));
	}
}