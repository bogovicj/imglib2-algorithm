package net.imglib2.algorithm.math.execution;

import java.util.Arrays;
import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.algorithm.math.abstractions.OFunction;
import net.imglib2.type.numeric.RealType;

public class NumericSource< O extends RealType< O > > implements OFunction< O >
{
	private final O value;
	private final Number number;
	
	public NumericSource( final O scrap, final Number number )
	{
		this.number = number;
		this.value = scrap;
		if ( number instanceof Float )
			this.value.setReal( number.floatValue() );
		else
			this.value.setReal( number.doubleValue() );
	}
	
	@Override
	public final O eval()
	{
		return this.value;
	}

	@Override
	public final O eval( final Localizable loc )
	{
		return this.value;
	}
	
	@Override
	public List< OFunction< O > > children()
	{
		return Arrays.asList();
	}
	
	@Override
	public final double evalDouble()
	{
		return this.number.doubleValue();
	}
	
	@Override
	public final double evalDouble( final Localizable loc )
	{
		return this.number.doubleValue();
	}
	
	@Override
	public boolean isOne()
	{
		return 1.0 == this.number.doubleValue();
	}
	
	@Override
	public boolean isZero()
	{
		return 0.0 == this.number.doubleValue();
	}
}
