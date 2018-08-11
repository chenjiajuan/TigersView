/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 * 
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.chenjiajuan.tigerview.game;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

public class WheelView extends View {
	private static final int ITEM_OFFSET_PERCENT = 0;
	private static final int PADDING = 0;
	private static final int DEF_VISIBLE_ITEMS = 5;
	private int currentItem = 0;
	private int visibleItems = DEF_VISIBLE_ITEMS;
	private int itemHeight = 0;
	private WheelScroller scroller;
	private boolean isScrollingPerformed;
	private int scrollingOffset;
	boolean isCyclic = false;
	private LinearLayout itemsLayout;
	private int firstItem;
	private WheelViewAdapter viewAdapter;
	private WheelRecycle recycle = new WheelRecycle(this);
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	public WheelView(Context context) {
		super(context);
		initData(context);
	}


	private void initData(Context context) {
		scroller = new WheelScroller(getContext(), scrollingListener);
	}

	WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {
		@Override
		public void onStarted() {
			isScrollingPerformed = true;
			scrollingListenerStarted();

		}

		@Override
		public void onScroll(int distance) {
			doScroll(distance);
			int height = getHeight();
			if (scrollingOffset > height) {
				scrollingOffset = height;
				scroller.stopScrolling();
			} else if (scrollingOffset < -height) {
				scrollingOffset = -height;
				scroller.stopScrolling();
			}
		}

		@Override
		public void onFinished() {
			if (isScrollingPerformed) {
				isScrollingPerformed = false;
			}

			scrollingOffset = 0;
			invalidate();
			scrollingListenerFinish();

		}

		@Override
		public void onJustify() {
			if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
				scroller.scroll(scrollingOffset, 0);
			}
		}
	};
	private OnWheelScrollListener scrollingListeners ;
	public void addScrollingListener(OnWheelScrollListener listener) {
		this.scrollingListeners=listener;
	}

	public void scrollingListenerStarted(){
		scrollingListeners.onScrollingStarted(this);
	}

	public void scrollingListenerFinish(){
		scrollingListeners.onScrollingFinished(this);
	}

	public void setInterpolator(Interpolator interpolator) {
		scroller.setInterpolator(interpolator);
	}

	public int getVisibleItems() {
		return visibleItems;
	}

	public void setVisibleItems(int count) {
		visibleItems = count;
	}

	public WheelViewAdapter getViewAdapter() {
		return viewAdapter;
	}

	private DataSetObserver dataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			invalidateWheel(false);
		}

		@Override
		public void onInvalidated() {
			invalidateWheel(true);
		}
	};

	public void setViewAdapter(WheelViewAdapter viewAdapter) {
		if (this.viewAdapter != null) {
			this.viewAdapter.unregisterDataSetObserver(dataObserver);
		}
		this.viewAdapter = viewAdapter;
		if (this.viewAdapter != null) {
			this.viewAdapter.registerDataSetObserver(dataObserver);
		}

		invalidateWheel(true);
	}



	public int getCurrentItem() {
		return currentItem;
	}


	public void setCurrentItem(int index, boolean animated) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return;
		}

		int itemCount = viewAdapter.getItemsCount();
		if (index < 0 || index >= itemCount) {
			if (isCyclic) {
				while (index < 0) {
					index += itemCount;
				}
				index %= itemCount;
			} else{
				return;
			}
		}
		if (index != currentItem) {
			if (animated) {
				int itemsToScroll = index - currentItem;
				if (isCyclic) {
					int scroll = itemCount + Math.min(index, currentItem) - Math.max(index, currentItem);
					if (scroll < Math.abs(itemsToScroll)) {
						itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
					}
				}
				scroll(itemsToScroll, 0);
			} else {
				scrollingOffset = 0;

				int old = currentItem;
				currentItem = index;


				invalidate();
			}
		}
	}

	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	public boolean isCyclic() {
		return isCyclic;
	}

	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
		invalidateWheel(true);
	}




	public void invalidateWheel(boolean clearCaches) {
		if (clearCaches) {
			recycle.clearAll();
			if (itemsLayout != null) {
				itemsLayout.removeAllViews();
			}
			scrollingOffset = 0;
		} else if (itemsLayout != null) {
			recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
		}

		invalidate();
	}



	private int getDesiredHeight(LinearLayout layout) {
		if (layout != null && layout.getChildAt(0) != null) {
			itemHeight = layout.getChildAt(0).getMeasuredHeight();
		}

		int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;
		return Math.max(desired, getSuggestedMinimumHeight());
	}

	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		}

		if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
			itemHeight = itemsLayout.getChildAt(0).getHeight();
			return itemHeight;
		}

		return getHeight() / visibleItems;
	}

	private int calculateLayoutWidth(int widthSize, int mode) {

		itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int width = itemsLayout.getMeasuredWidth();

		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width += 2 * PADDING;

			width = Math.max(width, getSuggestedMinimumWidth());
			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
			}
		}

		itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		return width;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		buildViewForMeasuring();
		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layout(r - l, b - t);
	}


	private void layout(int width, int height) {
		int itemsWidth = width - 2 * PADDING;
		itemsLayout.layout(0, 0, itemsWidth, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
			updateView();
			drawItems(canvas);
		}
	}


	private void drawItems(Canvas canvas) {
		canvas.save();
		int top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
		canvas.translate(PADDING, - top + scrollingOffset);
		itemsLayout.draw(canvas);
		canvas.restore();
	}

	private void doScroll(int delta) {
		scrollingOffset += delta;

		int itemHeight = getItemHeight();
		int count = scrollingOffset / itemHeight;

		int pos = currentItem - count;
		int itemCount = viewAdapter.getItemsCount();

		int fixPos = scrollingOffset % itemHeight;
		if (Math.abs(fixPos) <= itemHeight / 2) {
			fixPos = 0;
		}
		if (isCyclic && itemCount > 0) {
			if (fixPos > 0) {
				pos--;
				count++;
			} else if (fixPos < 0) {
				pos++;
				count--;
			}
			while (pos < 0) {
				pos += itemCount;
			}
			pos %= itemCount;
		} else {
			//
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= itemCount) {
				count = currentItem - itemCount + 1;
				pos = itemCount - 1;
			} else if (pos > 0 && fixPos > 0) {
				pos--;
				count++;
			} else if (pos < itemCount - 1 && fixPos < 0) {
				pos++;
				count--;
			}
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		// update offset
		scrollingOffset = offset - count * itemHeight;
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	public void scroll(int itemsToScroll, int time) {
		int distance = itemsToScroll * getItemHeight() - scrollingOffset;
		scroller.scroll(distance, time);
	}

	private ItemsRange getItemsRange() {
		if (getItemHeight() == 0) {
			return null;
		}

		int first = currentItem;
		int count = 1;

		while (count * getItemHeight() < getHeight()) {
			first--;
			count += 2;
		}

		if (scrollingOffset != 0) {
			if (scrollingOffset > 0) {
				first--;
			}
			count++;

			int emptyItems = scrollingOffset / getItemHeight();
			first -= emptyItems;
			count += Math.asin(emptyItems);
		}
		return new ItemsRange(first, count);
	}

	private boolean rebuildItems() {
		boolean updated = false;
		ItemsRange range = getItemsRange();
		if (itemsLayout != null) {
			int first = recycle.recycleItems(itemsLayout, firstItem, range);
			updated = firstItem != first;
			firstItem = first;
		} else {
			createItemsLayout();
			updated = true;
		}

		if (!updated) {
			updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
		}

		if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
			for (int i = firstItem - 1; i >= range.getFirst(); i--) {
				if (!addViewItem(i, true)) {
					break;
				}
				firstItem = i;
			}
		} else {
			firstItem = range.getFirst();
		}

		int first = firstItem;
		for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
			if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
				first++;
			}
		}
		firstItem = first;

		return updated;
	}

	private void updateView() {
		if (rebuildItems()) {
			calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			layout(getWidth(), getHeight());
		}
	}

	private void createItemsLayout() {
		if (itemsLayout == null) {
			itemsLayout = new LinearLayout(getContext());
			itemsLayout.setOrientation(LinearLayout.VERTICAL);
		}
	}


	private void buildViewForMeasuring() {
		if (itemsLayout != null) {
			recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
		} else {
			createItemsLayout();
		}

		int addItems = visibleItems / 2;
		for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
			if (addViewItem(i, true)) {
				firstItem = i;
			}
		}
	}

	private boolean addViewItem(int index, boolean first) {
		View view = getItemView(index);
		if (view != null) {
			if (first) {
				itemsLayout.addView(view, 0);
			} else {
				itemsLayout.addView(view);
			}

			return true;
		}

		return false;
	}


	private boolean isValidItemIndex(int index) {
		return viewAdapter != null && viewAdapter.getItemsCount() > 0 &&
				(isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
	}
	private View getItemView(int index) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return null;
		}
		int count = viewAdapter.getItemsCount();
		if (!isValidItemIndex(index)) {
			return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
	}

	/**
	 * Stops scrolling
	 */
	public void stopScrolling() {
		scroller.stopScrolling();
	}
}
