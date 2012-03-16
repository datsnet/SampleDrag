package com.blahti.example.drag3;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImageLinearLayout extends FrameLayout implements DragSource,
		DropTarget, Cloneable {

	public boolean mEmpty = true;
	public int mCellNumber = -1;
	public GridView mGrid;
	public ImageCell leftImage;
	public ImageCell rightImage;
	public int targetId;
	public int sourceId;
	private ImageCellAdapter mAdapter;
	private int LEFT_IMAGECELL_INDEX = 0;
	private int RIGHT_IMAGECELL_INDEX = 1;
	private int LAYOUT_CONDITION_INDEX = 0;
	private int erapsedCount;
	private DragController mDragController;


	public ImageLinearLayout(Context context) {
		super(context);
	}

	public ImageLinearLayout(Context context, ImageCellAdapter adapter) {
		super(context);
		this.mAdapter = adapter;
	}

	public ImageLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public ImageLinearLayout clone() {
        try {
        	ImageLinearLayout myClone = (ImageLinearLayout)super.clone();
        	LinearLayout parentView = (LinearLayout) myClone.getChildAt(LAYOUT_CONDITION_INDEX);
            //参照型のフィールドにもclone()
//        	myClone.leftImage = (ImageCell) ((ImageCell)myClone.getChildAt(LEFT_IMAGECELL_INDEX)).clone();
        	myClone.leftImage = (ImageCell) ((ImageCell)parentView.getChildAt(LEFT_IMAGECELL_INDEX)).clone();
        	myClone.rightImage = (ImageCell) ((ImageCell)parentView.getChildAt(RIGHT_IMAGECELL_INDEX)).clone();
        	myClone.targetId = this.sourceId;
            return myClone;
        } catch (CloneNotSupportedException e) {
        	e.printStackTrace();
            return null;
        }

    }

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// Mark the cell so it is no longer empty.
		mEmpty = false;
		ImageLinearLayout sourceView = (ImageLinearLayout) source;
		LinearLayout parentView = (LinearLayout) sourceView.getChildAt(LAYOUT_CONDITION_INDEX);
		LinearLayout parentSource = (LinearLayout) this.getChildAt(LAYOUT_CONDITION_INDEX);
		ImageCell leftView = null;
		ImageCell rightView = null;
		leftView = (ImageCell) parentView.getChildAt(LEFT_IMAGECELL_INDEX);
		rightView = (ImageCell) parentView.getChildAt(RIGHT_IMAGECELL_INDEX);

		Drawable dLeft = leftView.getDrawable();
		Drawable dRight = rightView.getDrawable();
		((ImageView) parentSource.getChildAt(LEFT_IMAGECELL_INDEX)).setImageDrawable(dLeft);
		((ImageView) parentSource.getChildAt(RIGHT_IMAGECELL_INDEX)).setImageDrawable(dRight);

		stopScroll();

	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		// If the drop succeeds, the image has moved elsewhere.
		// So clear the image cell.
		stopScroll();
		if (success) {

			ImageLinearLayout targetView = (ImageLinearLayout) target;

			Drawable dLeft = targetView.leftImage.getDrawable();
			Drawable dRight = targetView.rightImage.getDrawable();
			LinearLayout parentView = (LinearLayout) this.getChildAt(LAYOUT_CONDITION_INDEX);
			((ImageView) parentView.getChildAt(LEFT_IMAGECELL_INDEX)).setImageDrawable(dLeft);
			((ImageView) parentView.getChildAt(RIGHT_IMAGECELL_INDEX)).setImageDrawable(dRight);

//			int i = 0;
//			while(5 > i) {
//				Log.i("111", this.mAdapter.mImageList.get(i).getLeftImagePath());
//				Log.i("R111", this.mAdapter.mImageList.get(i).getRightImagePath());
//				i++;
//			}

			// 格納ブックオブジェクトの交換
			BookImage sourceBook = this.mAdapter.mImageList.get(this.sourceId);
			BookImage targetBook = this.mAdapter.mImageList.get(targetView.mCellNumber);

			// 位置交換
			this.mAdapter.mImageList.set(targetView.mCellNumber , sourceBook);
			this.mAdapter.mImageList.set(this.sourceId , targetBook);

			this.mAdapter.notifyDataSetChanged();
//			int j = 0;
//			while(5 > j) {
//				Log.i("555", this.mAdapter.mImageList.get(j).getLeftImagePath());
//				Log.i("R555", this.mAdapter.mImageList.get(j).getRightImagePath());
//				j++;
//			}

		}

	}
	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo, int xMove, int yMove) {
//		setBackgroundResource(R.color.cell_empty_hover);
		ImageLinearLayout dragInfoLayout = (ImageLinearLayout) dragInfo;
		LinearLayout layout = (LinearLayout)dragInfoLayout.getChildAt(LAYOUT_CONDITION_INDEX);
		layout.setBackgroundColor(R.color.cell_empty_hover);
		layout.getChildAt(LEFT_IMAGECELL_INDEX).setBackgroundColor(R.color.cell_empty_hover);
		layout.getChildAt(RIGHT_IMAGECELL_INDEX).setBackgroundColor(R.color.cell_empty_hover);

		this.sourceId = dragInfoLayout.mCellNumber;



	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo, int xMove, int yMove) {
		//画面端ならスクロール
		if(yMove - ((View) dragView).getHeight() < 0){
			Log.i("DragInfo", String.valueOf(((View) dragView).getHeight()/2));
			isLoop = true;
			ishighLoop = true;
			startPrevScroll();
			mDragController.setErapsedCount();
		} else if(yMove + ((View) dragView).getHeight() > getRootView().getHeight()){
			Log.i("DragInfo", String.valueOf(((View) dragView).getHeight()/2));
			isLoop = true;
			ishighLoop = true;
			startNextScroll();

			mDragController.setErapsedCount();
		} else{
			stopScroll();
		}
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo, int xMove, int yMove) {
		setBackgroundResource(0);
		if (yMove - ((View) dragView).getHeight() < 0) {
			return;
		} else if (yMove + ((View) dragView).getHeight() > getRootView()
				.getHeight()) {
			return;
		} else {
			stopScroll();
		}

	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// return mEmpty && (mCellNumber >= 0);
		return true;
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		return null;
	}

	/**
	 * Return true if this cell is empty. If it is, it means that it will accept
	 * dropped views. It also means that there is nothing to drag.
	 *
	 * @return boolean
	 */

	public boolean isEmpty() {
		return mEmpty;
	}

	/**
	 * setDragController
	 *
	 */
	public void setDragController(DragController dragger) {
		mDragController = dragger;
	}

	public boolean performClick() {
		if (!mEmpty)
			return super.performClick();
		return false;
	}

	/**
	 * Call this view's onLongClick listener. Return true if it was called.
	 * Clicks are ignored if the cell is empty.
	 *
	 * @return boolean
	 */

	public boolean performLongClick() {
		if (!mEmpty)
			return super.performLongClick();
		return super.performLongClick();
		// return false;
	}

	public void toast(String msg) {
		if (!DragActivity.Debugging)
			return;
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * リストビューの取得
	 * @return
	 */
	private AbsListView getGridView() {
		return mGrid;
	}

	public void setErapsedCount() {
		this.erapsedCount++;
	}

	// スクロール処理系
	private void startPrevScroll() {

	    // 経過カウント数によってスクロールスピードを切り替え
		if (mDragController.getErapsedCount() >= 20) {
			getHandler().postDelayed(highSpeedPrevScroll, 200);
			isLoop = false;
		} else {
			getHandler().postDelayed(prevScroll, 200);
			ishighLoop = false;
		}
	}

	private void startNextScroll() {

	    // 経過カウント数によってスクロールスピードを切り替え
		if (mDragController.getErapsedCount() >= 20) {
			getHandler().postDelayed(highSpeedNextScroll, 200);
			isLoop = false;
		} else {
			getHandler().postDelayed(nextScroll, 200);
			ishighLoop = false;
		}

	}

	public void stopScroll() {
		mDragController.resetErapsedCount();
		ishighLoop = false;
		isLoop = false;
	}

	public boolean isLoop = false;
	public boolean ishighLoop = false;
	private Runnable prevScroll = new Runnable() {
		@Override
		public void run() {
			getGridView().smoothScrollBy(-10, 200);
			if (isLoop) {
				startPrevScroll();
			} else {
//				Log.i("isLoop", String.valueOf(isLoop));
			}
		}
	};
	private Runnable highSpeedPrevScroll = new Runnable() {
		@Override
		public void run() {
			getGridView().smoothScrollBy(-50, 10);
			if (ishighLoop) {
				startPrevScroll();
			} else {
//				Log.i("isLoop", String.valueOf(isLoop));
			}
		}
	};
	private Runnable nextScroll = new Runnable() {
		@Override
		public void run() {
			getGridView().smoothScrollBy(10, 200);
			if (isLoop) {
				startNextScroll();
			} else {
//				Log.i("isLoop", String.valueOf(isLoop));
			}
		}
	};

	private Runnable highSpeedNextScroll = new Runnable() {
		@Override
		public void run() {
			getGridView().smoothScrollBy(50, 10);
			if (ishighLoop) {
				startNextScroll();
			} else {
//				Log.i("ishighLoop", String.valueOf(ishighLoop));
			}
		}
	};

	private boolean isScrollArea() {
		return isLoop;

	}
}
