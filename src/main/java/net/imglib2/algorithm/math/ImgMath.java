package net.imglib2.algorithm.math;

import net.imglib2.Interval;
import net.imglib2.KDTree;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.math.abstractions.IFunction;
import net.imglib2.algorithm.math.abstractions.Util;
import net.imglib2.converter.Converter;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * An easy yet high performance way to perform pixel-wise math
 * on one or more {@link RandomAccessibleInterval} instances.
 * 
 * Peak performance is within 1.2X - 1.8X of manually writing a loop
 * using ImgLib2's low-level {@code Cursor} or {@code RandomAccess} with
 * native math {@code + / - *} and flow {@code if else} operators
 * and in-loop variable declarations.
 * 
 * Input images can be of different {@code Type} as long as all of them
 * extend RealType.
 * 
 * An example in java:
 * 
 * <pre>
 * {@code
 * 
 * import static net.imglib2.algorithm.math.ImgMath.*;
 * 
 * RandomAccessibleInterval<A> img1 = ...
 * RandomAccessibleInterval<B> img2 = ...
 * RandomAccessibleInterval<C> img3 = ...
 * 
 * RandomAccessibleInterval<O> result = ...
 * 
 * compute( div( max( img1, img2, img3 ), 3.0 ) ).into( result );
 * }
 * </pre>
 * 
 * Another example, illustrating variable declaration with {@code Let}
 * and also if/then/else flow control, to compute the saturation of
 * an RGB image:
 * 
 * <pre>
 * {@code
 * import static net.imglib2.algorithm.math.ImgMath.*;
 * 
 * RandomAccessible< ARGBType > rgb = ...
 * 
 * final RandomAccessibleInterval< UnsignedByteType >
 *		    red = Converters.argbChannel( rgb, 1 ),
 *			green = Converters.argbChannel( rgb, 2 ),
 *			blue = Converters.argbChannel( rgb, 3 );
 * 
 * RandomAccessibleInterval< FloatType > saturation = new ArrayImgFactory< FloatType >( new FloatType() ).create( rgb );
 * 
 * compute( let( "red", red,
 *				 "green", green,
 *				 "blue", blue,
 *				 "max", max( var( "red" ), var( "green" ), var( "blue" ) ),
 *				 "min", min( var( "red" ), var( "green" ), var( "blue" ) ),
 *				 IF ( EQ( 0, var( "max" ) ),
 *				   THEN( 0 ),
 *				   ELSE( div( sub( var( "max" ), var( "min" ) ),
 *					          var( "max" ) ) ) ) ) )
 *		  .into( saturation );
 * 
 * }
 * </pre>
 * 
 * @author Albert Cardona
 *
 */
public class ImgMath
{
	static public final Compute compute( final IFunction operation )
	{
		return new Compute( operation );
	}
	
	static public final < I extends RealType< I > > Compute compute( final RandomAccessibleInterval< I > src )
	{
		return compute( img( src ) );
	}
	
	static public final RandomAccessibleInterval< FloatType > computeIntoFloat( final IFunction operation )
	{
		return new Compute( operation ).into( new ArrayImgFactory< FloatType >( new FloatType() ).create( Util.findImg( operation ).iterator().next() ) );
	}
	
	static public final < O extends RealType< O > > RandomAccessibleInterval< O > computeInto(
			final IFunction operation,
			final RandomAccessibleInterval< O > target )
	{
		return new Compute( operation ).into( target );
	}

	static public final < O extends RealType< O > > RandomAccessibleInterval< O > computeInto(
			final IFunction operation,
			final RandomAccessibleInterval< O > target,
			final Converter< RealType< ? >, O > converter )
	{
		return new Compute( operation ).into( target, converter );
	}
	
	static public final < O extends NativeType< O > & RealType< O > > RandomAccessibleInterval< O > computeIntoImg( final IFunction operation )
	{
		return compute( operation ).intoImg();
	}
	
	static public final < O extends NativeType< O > & RealType< O > > RandomAccessibleInterval< O > computeIntoArrayImg( final IFunction operation )
	{
		return compute( operation ).intoArrayImg();
	}
	
	static public final Add add( final Object o1, final Object o2 )
	{
		return new Add( o1, o2 );
	}
	
	static public final Add add( final Object... obs )
	{
		return new Add( obs );
	}
	
	static public final Sub sub( final Object o1, final Object o2 )
	{
		return new Sub( o1, o2 );
	}
	
	static public final Sub sub( final Object... obs )
	{
		return new Sub( obs );
	}
	
	static public final Minus minus( final Object o1 )
	{
		return new Minus( o1 );
	}
	
	static public final Mul mul( final Object o1, final Object o2 )
	{
		return new Mul( o1, o2 );
	}
	
	static public final Mul mul( final Object... obs )
	{
		return new Mul( obs );
	}
	
	static public final Div div( final Object o1, final Object o2 )
	{
		return new Div( o1, o2 );
	}
	
	static public final Div div( final Object... obs )
	{
		return new Div( obs );
	}
	
	static public final Pow pow( final Object o1, final Object o2 )
	{
		return new Pow( o1, o2 );
	}
	
	static public final Pow power( final Object... obs )
	{
		return new Pow( obs );
	}
	
	static public final Pow power( final Object o1, final Object o2 )
	{
		return new Pow( o1, o2 );
	}
	
	static public final Pow pow( final Object... obs )
	{
		return new Pow( obs );
	}

	static public final Max max( final Object o1, final Object o2 )
	{
		return new Max( o1, o2 );
	}
	
	static public final Max max( final Object... obs )
	{
		return new Max( obs );
	}
	
	static public final Max maximum( final Object o1, final Object o2 )
	{
		return new Max( o1, o2 );
	}
	
	static public final Max maximum( final Object... obs )
	{
		return new Max( obs );
	}
	
	static public final Min min( final Object o1, final Object o2 )
	{
		return new Min( o1, o2 );
	}
	
	static public final Min min( final Object... obs )
	{
		return new Min( obs );
	}
	
	static public final Min minimum( final Object o1, final Object o2 )
	{
		return new Min( o1, o2 );
	}
	
	static public final Min minimum( final Object... obs )
	{
		return new Min( obs );
	}
	
	static public final Log log( final Object o1 )
	{
		return new Log( o1 );
	}
	
	static public final Log logarithm( final Object o1 )
	{
		return new Log( o1 );
	}
	
	static public final Exp exp( final Object o1 )
	{
		return new Exp( o1 );
	}
	
	static public final Let let( final String varName, final Object varValue, final Object body )
	{
		return new Let( varName, varValue, body );
	}
	
	static public final Let let( final Object[] pairs, final Object body )
	{
		return new Let( pairs, body );
	}
	
	static public final Let let( final Object... obs )
	{
		return new Let( obs );
	}
	
	static public final Var var( final String name )
	{
		return new Var( name );
	}
	
	static public final Equal EQ( final Object o1, final Object o2 )
	{
		return new Equal( o1, o2 );
	}
	
	static public final Equal equal( final Object o1, final Object o2 )
	{
		return new Equal( o1, o2 );
	}
	
	static public final NotEqual NEQ( final Object o1, final Object o2 )
	{
		return new NotEqual( o1, o2 );
	}
	
	static public final NotEqual notEqual( final Object o1, final Object o2 )
	{
		return new NotEqual( o1, o2 );
	}
	
	static public final LessThan LT( final Object o1, final Object o2 )
	{
		return new LessThan( o1, o2 );
	}
	
	static public final LessThan lessThan( final Object o1, final Object o2 )
	{
		return new LessThan( o1, o2 );
	}
	
	static public final GreaterThan GT( final Object o1, final Object o2 )
	{
		return new GreaterThan( o1, o2 );
	}
	
	static public final GreaterThan greaterThan( final Object o1, final Object o2 )
	{
		return new GreaterThan( o1, o2 );
	}
	
	static public final If IF( final Object o1, final Object o2, final Object o3 )
	{
		return new If( o1, o2, o3 );
	}
	
	static public final Then THEN( final Object o )
	{
		return new Then( o );
	}
	
	static public final Else ELSE( final Object o )
	{
		return new Else( o );
	}
	
	static public final < T extends RealType< T > > ImgSource< T > img( final RandomAccessibleInterval< T > rai )
	{
		return new ImgSource< T >( rai );
	}
	
	/** Synonym of {@code img(RandomAccessibleInterval)}, given that {@code img} is a widely used variable name. */
	static public final < T extends RealType< T > > ImgSource< T > intervalSource( final RandomAccessibleInterval< T > rai )
	{
		return new ImgSource< T >( rai );
	}
	
	static public final NumberSource number( final Number number )
	{
		return new NumberSource( number );
	}
	
	static public final < T extends RealType< T > > BlockRead< T > block( final RandomAccessible< T > src, final long[] radius )
	{
		return new BlockRead< T >( src, radius );
	}
	
	static public final < T extends RealType< T > > BlockRead< T > block( final RandomAccessible< T > src, final long radius )
	{
		return new BlockRead< T >( src, radius );
	}
	
	static public final < T extends RealType< T > > BlockRead< T > block( final RandomAccessible< T > src, final long[][] corners )
	{
		return new BlockRead< T >( src, corners );
	}
	
	static public final < T extends RealType< T > > RandomAccessibleSource< T > offset( final RandomAccessible< T > src, final long[] offset )
	{
		return new RandomAccessibleSource< T >( src, offset );
	}
	
	static public final < T extends RealType< T > > IFunction source( final RandomAccessible< T > src )
	{
		if ( src instanceof RandomAccessibleInterval< ? > )
			return intervalSource( ( RandomAccessibleInterval< T > )src );
		return new RandomAccessibleSource< T >( src );
	}
	
	static public final < T extends RealType< T > > KDTreeSource< T > gen( final KDTree< T > kdtree, final double radius )
	{
		return new KDTreeSource< T >( kdtree, radius );
	}
	
	static public final < T extends RealType< T > > KDTreeSource< T > gen( final KDTree< T > kdtree, final double radius, final Object outside )
	{
		return new KDTreeSource< T >( kdtree, radius, outside );
	}
	
	static public final < T extends RealType< T > > KDTreeSource< T > gen( final KDTree< T > kdtree, final double radius, final Object outside, final Interval interval )
	{
		return new KDTreeSource< T >( kdtree, radius, outside, interval );
	}
}
