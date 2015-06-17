/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.algorithm.neighborhood;

import java.util.Iterator;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;

/**
 * A factory for Accessibles on rectangular neighboorhoods.
 * 
 * TODO: support non-isotropic, non-symmetric rectangular neighboorhood shapes.
 * (the Neighborhood implementation supports it already, we just need to change
 * this factory.)
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author John Bogovic <bogovic@gmail.com>
 */
public class CrossShape implements Shape
{
	final int span;

	final boolean skipCenter;

	/**
	 * @param span
	 * @param skipCenter
	 */
	public CrossShape( final int span, final boolean skipCenter )
	{
		this.span = span;
		this.skipCenter = skipCenter;
	}

	public CrossShape( final int span )
	{
		this( span, false );
	}
	
	public < T > NeighborhoodsAccessible< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return neighborhoodsRandomAccessible( source );
	}

	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessibleInterval< T > source )
	{
		final CrossNeighborhoodFactory< T > f = skipCenter ? CrossNeighborhoodSkipCenterUnsafe.< T >factory() : CrossNeighborhoodUnsafe.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	public < T > NeighborhoodsAccessible< T > neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return neighborhoodsRandomAccessibleSafe( source );
	}

	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source )
	{
		final CrossNeighborhoodFactory< T > f = skipCenter ? CrossNeighborhoodSkipCenter.< T >factory() : CrossNeighborhood.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	private Interval createSpan( final int n )
	{
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = -span;
			max[ d ] = span;
		}
		return new FinalInterval( min, max );
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >, IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final Interval span;

		final CrossNeighborhoodFactory< T > factory;

		final long size;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final Interval span, final CrossNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
				s *= source.dimension( d );
			size = s;
		}

		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new CrossNeighborhoodRandomAccess< T >( source, span, factory );
		}


		public Cursor< Neighborhood< T >> cursor()
		{
			return new CrossNeighborhoodCursor< T >( source, span, factory );
		}

		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		public long size()
		{
			return size;
		}

		public Neighborhood< T > firstElement()
		{
			return cursor().next();
		}

		public Object iterationOrder()
		{
			return new FlatIterationOrder( this );
		}

		public boolean equalIterationOrder( final IterableRealInterval< ? > f )
		{
			return iterationOrder().equals( f.iterationOrder() );
		}

		public Iterator< Neighborhood< T >> iterator()
		{
			return cursor();
		}

		public Cursor< Neighborhood< T >> localizingCursor()
		{
			return cursor();
		}
	}

	@Override
	public <T> RandomAccessible< Neighborhood< T >> neighborhoodsRandomAccessible(
			RandomAccessible< T > arg0 )
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <T> RandomAccessible< Neighborhood< T >> neighborhoodsRandomAccessibleSafe(
			RandomAccessible< T > arg0 )
	{
		// TODO Auto-generated method stub
		return null;
	}
}
