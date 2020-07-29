package net.imglib2.algorithm.math.abstractions;

import java.util.List;

import net.imglib2.Localizable;
import net.imglib2.type.numeric.RealType;

public interface OFunction< O extends RealType< O > >
{
	public O eval();
	
	public O eval( final Localizable loc );
	
	public List< OFunction< O > > children();
	
	public double evalDouble();
	
	public double evalDouble( final Localizable loc );
}
