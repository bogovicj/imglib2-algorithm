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


import net.imglib2.Interval;
import net.imglib2.RandomAccess;

public class CrossNeighborhood< T > extends CrossNeighborhoodSkipCenter<T>
{
	public static < T > CrossNeighborhoodFactory< T > factory()
	{
		return new CrossNeighborhoodFactory< T >() {
			
			public Neighborhood< T > create( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess )
			{
				return new CrossNeighborhood< T >( position, currentMin, currentMax, span, sourceRandomAccess );
			}
		};
	}


	CrossNeighborhood( final long[] position, final long[] currentMin, final long[] currentMax, final Interval span, final RandomAccess< T > sourceRandomAccess )
	{
		
		super( position, currentMin, currentMax, span, sourceRandomAccess);
		size++; // for the center
		
		
	}
	
	@Override
	public LocalCrossCursor cursor()
	{
		return new LocalCrossCursorCursorIC( sourceRandomAccess.copyRandomAccess() );
	}

	@Override
	public LocalCrossCursor localizingCursor()
	{
		return cursor();
	}


	public final class LocalCrossCursorCursorIC extends LocalCrossCursor
	{

		boolean atCenterStart = true;
		boolean switchToSuper = false;
		
		public LocalCrossCursorCursorIC( final RandomAccess< T > source )
		{
			super( source );
		}

		protected LocalCrossCursorCursorIC( final LocalCrossCursor c )
		{
			super( c );
		}


		@Override
		public void fwd()
		{
			if(atCenterStart){
				source.fwd(0);
				atCenterStart = false;
				switchToSuper = true;
				
			}else if(!atCenterStart && switchToSuper){
				
				atCenterStart = false;
				switchToSuper = false;
				
				super.reset();
				super.fwd();
				
			}else{
				super.fwd();
			}
		}

		@Override
		public void reset()
		{
			atCenterStart = true;
			switchToSuper = false;
			source.setPosition(position);
			source.bck(0);
			
		}

		@Override
		public boolean hasNext()
		{
			return (curdim < dimensions.length - 1) || ( index < maxIndexOnLine - 1 ); 
		}

		@Override
		public LocalCrossCursorCursorIC copy()
		{
			return new LocalCrossCursorCursorIC( this );
		}

		@Override
		public LocalCrossCursorCursorIC copyCursor()
		{
			return copy();
		}
	}
}
