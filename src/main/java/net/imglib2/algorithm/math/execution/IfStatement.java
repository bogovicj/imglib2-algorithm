package net.imglib2.algorithm.math.execution;

import java.util.Arrays;
import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.algorithm.math.abstractions.OFunction;
import net.imglib2.type.numeric.RealType;

public class IfStatement< O extends RealType< O > > implements OFunction< O >
{
	private final OFunction< O > a, b, c;
	
	public IfStatement( final OFunction< O > f1, final OFunction< O > f2, final OFunction< O > f3 )
	{
		this.a = f1;
		this.b = f2;
		this.c = f3;
	}
	
	@Override
	public final O eval()
	{
		return 0 != this.a.eval().getRealFloat() ?
			// Then
			this.b.eval()
			// Else
			: this.c.eval();
	}

	@Override
	public final O eval( final Localizable loc )
	{
		return 0 != this.a.eval( loc ).getRealFloat() ?
			// Then
			this.b.eval( loc )
			// Else
			: this.c.eval( loc );
	}
	
	@Override
	public List< OFunction< O > > children()
	{
		return Arrays.asList( this.a, this.b, this.c );
	}
	
	
	@Override
	public final double evalDouble()
	{
		return 0 != this.a.evalDouble() ?
			// Then
			this.b.evalDouble()
			// Else
			: this.c.evalDouble();
	}
	
	@Override
	public final double evalDouble( final Localizable loc )
	{
		return 0 != this.a.evalDouble( loc ) ?
			// Then
			this.b.evalDouble( loc )
			// Else
			: this.c.evalDouble( loc );	}
}
