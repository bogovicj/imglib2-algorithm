/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Dimiter Prodanov, Curtis Rueden,
 * Johannes Schindelin, Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.algorithm.stats;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import net.imglib2.view.composite.Composite;

/**
 * Implements an n-dimensional joint histogram over a set of images
 * 
 * 
 * @author Larry Lindsey
 * @author John Bogovic
 */
public class JointHistogram< T extends Type<T>, I extends NumericType<I> > implements Algorithm, Benchmark
{
	/**
	 * Processing time, milliseconds.
	 */
	private long pTime = 0;

	/**
	 * Hold the histogram itself.
	 */
	private final RandomAccessibleInterval<I> histogram;

	/**
	 * 
	 */
	private final RandomAccessibleInterval<T> img;

	/**
	 * The HistogramBinMapper, used to map Type values to histogram bin indices.
	 */
	private final List< HistogramBinMapper< T >> binMapperList;
	
	private final I one;

	/**
	 * Create a Histogram using the given mapper, calculating from the given
	 * Cursor.
	 * 
	 * @param mapper
	 *            the HistogramBinMapper used to map Type values to histogram
	 *            bin indices.
	 * @param c
	 *            a Cursor corresponding to the Image from which the Histogram
	 *            will be calculated
	 * 
	 */
	public JointHistogram( final List<HistogramBinMapper< T >> mapperList,
			final RandomAccessibleInterval< T > img,
			ImgFactory< I > factory,
			I countType )
	{
		this.img = img;
		binMapperList = mapperList;
		
		long[] histSz = new long[ mapperList.size() ];
		int i = 0;
		for( HistogramBinMapper<?> m : mapperList )
			histSz[ i++ ] = m.getNumBins();
		
		histogram = factory.create( histSz, countType );
		
		one = countType.copy();
		one.setOne();
	}

	/**
	 * Create a Histogram using the given mapper, calculating from the given
	 * Image.
	 * 
	 * @param mapper
	 *            the HistogramBinMapper used to map Type values to histogram
	 *            bin indices.
	 * @param image
	 *            an Image from which the Histogram will be calculated
	 * 
	 */
	public JointHistogram( final List<HistogramBinMapper< T >> mapper,
			final Img< T > image )
	{
		this( null, null, null, null );
//		this( mapper, Arrays.asList( new RealCursor[]{ image.cursor() }), new ArrayImgFactory<IntType>(), new IntType() );
//		this( mapper, Arrays.asList( new RealCursor[]{ image.cursor() }), null, null );
//		Arrays.asList( new RealCursor[]{ image.cursor() });
	}

	/**
	 * Returns the bin count corresponding to a given {@link Type}.
	 * 
	 * @param t
	 *            the Type corresponding to the requested
	 * @return The requested bin count.
	 */
	@SuppressWarnings("unchecked")
	public I getBin(  final T... ts )
	{
		int[] out = new int[ histogram.numDimensions() ];
		int i = 0;
		for( T t : ts )
		{
			out[ i ] = binMapperList.get( i ).map( t );
			i++;
		}
		
		RandomAccess< I > hra = histogram.randomAccess();
		hra.setPosition( out );
		
		return hra.get();
	}

	/**
	 * Returns the bin count given by the indicated bin index.
	 * 
	 * @param i
	 *            the index of the requested bin
	 * @return the bin count at the given index
	 */
	public I getBin( final int... pos )
	{
		RandomAccess< I > ra = histogram.randomAccess(); 
		ra.setPosition( pos );
		return ra.get();
	}

	/**
	 * Returns this Histogram's HistogramBinMapper.
	 * 
	 * @return the HistogramBinMapper associated with this Histogram.
	 */
	public List<HistogramBinMapper< T >> getBinMappers( )
	{
		return binMapperList;
	}

	/**
	 * Returns the histogram array.
	 * 
	 * @return the histogram array.
	 */
	public RandomAccessibleInterval<I> getHistogram()
	{
		return histogram;
	}

	/**
	 * Creates and returns the a Type whose value corresponds to the center of
	 * the bin indexed by i.
	 * 
	 * @param i
	 *            the requested bin index.
	 * @return a Type whose value corresponds to the requested bin center.
	 */
	public List<T> getBinCenter( final int... i )
	{
		
		List<T> out = new ArrayList<T>( getBinMappers().size() );
		int j = 0;
		for( HistogramBinMapper< T > m : getBinMappers())
		{
			out.add( m.invMap( i[ j++ ] ));
		}

		return out;
	}

	/**
	 * Creates and returns a List containing Types that correspond to the
	 * centers of the histogram bins.
	 * 
	 * @return an Img containing Types that correspond to the centers of the
	 *         histogram bins.
	 */
	public Img<T> getBinCenters()
	{
		ListImgFactory<T> factory = new ListImgFactory<T>();
		
		int nd = histogram.numDimensions();
		long[] centersSz = new long[ nd + 1 ];
		histogram.dimensions( centersSz );
		centersSz[ nd ] = nd;
		
		Img< T > binCenters = factory.create( centersSz, Views.flatIterable( img ).firstElement() );
		RandomAccess< T > bcra = binCenters.randomAccess();

		Cursor< I > hc = Views.flatIterable( histogram ).cursor();
		int[] histPos = new int[ histogram.numDimensions() ]; 
		while( hc.hasNext() )
		{
			hc.fwd();
			hc.localize( histPos );
			bcra.setPosition( hc );
			List<T> binCenterList = getBinCenter( histPos );

			for( int i = 0; i < nd; i++ )
			{
				bcra.setPosition( i, nd );
				bcra.get().set( binCenterList.get( i ) );
			}
		}
		return binCenters;
	}

	/**
	 * Returns the number of bins in this Histogram.
	 * 
	 * @return the number of bins in this Histogram
	 * 
	 */
	public int[] getNumBins()
	{
		return Intervals.dimensionsAsIntArray( histogram );
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return null;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		
		Cursor< Composite< T > > cursor = (Cursor< Composite< T >>) Views.flatIterable( Views.collapse( img ) ).cursor();
		int[] bin = new int[ histogram.numDimensions() ];
		RandomAccess< I > hra = histogram.randomAccess();

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			map( cursor.get(), bin );

			hra.setPosition( bin );
			hra.get().add( one ); // increment

//			/*
//			 * The following check makes this run for IntegerTypes at 3 to 4
//			 * longer than the manual case on my machine. This is a necessary
//			 * check, but if this takes too long, it might be worthwhile to
//			 * separate out an UncheckedHistogram, which would instead throw an
//			 * ArrayOutOfBoundsException.
//			 */
//			if ( index >= 0 && index < histogram.length )
//			{
//				++histogram[ index ];
//			}
		}

		pTime = System.currentTimeMillis() - startTime;
		return true;
	}
	
	
	/**
	 * Maps a value into its corresponding bin
	 * @param t
	 * @param bin array in which to store the bin location 
	 */
	public void map( Composite<T> tvec, int[] bin )
	{
		int i = 0;
		for( HistogramBinMapper<T> binMapper : getBinMappers() )
		{
			bin[ i ] = binMapper.map( tvec.get( i ));
			i++;
		}
	}

	@Override
	public long getProcessingTime()
	{
		return pTime;
	}

}
