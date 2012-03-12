package com.blahti.example.drag3;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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
	private int RIGHT_IMAGECELL_INDEX = 2;


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
            //参照型のフィールドにもclone()
        	myClone.leftImage = (ImageCell) ((ImageCell)myClone.getChildAt(LEFT_IMAGECELL_INDEX)).clone();
        	myClone.rightImage = (ImageCell) ((ImageCell)myClone.getChildAt(RIGHT_IMAGECELL_INDEX)).clone();
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
		ImageCell leftView = null;
		ImageCell rightView = null;
		leftView = (ImageCell) sourceView.getChildAt(LEFT_IMAGECELL_INDEX);
		rightView = (ImageCell) sourceView.getChildAt(RIGHT_IMAGECELL_INDEX);

		Drawable dLeft = leftView.getDrawable();
		Drawable dRight = rightView.getDrawable();
		((ImageView) this.getChildAt(LEFT_IMAGECELL_INDEX)).setImageDrawable(dLeft);
		((ImageView) this.getChildAt(RIGHT_IMAGECELL_INDEX)).setImageDrawable(dRight);
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		// If the drop succeeds, the image has moved elsewhere.
		// So clear the image cell.
		if (success) {

			ImageLinearLayout targetView = (ImageLinearLayout) target;

			Drawable dLeft = targetView.leftImage.getDrawable();
			Drawable dRight = targetView.rightImage.getDrawable();
			((ImageView) this.getChildAt(LEFT_IMAGECELL_INDEX)).setImageDrawable(dLeft);
			((ImageView) this.getChildAt(RIGHT_IMAGECELL_INDEX)).setImageDrawable(dRight);

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
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(R.color.cell_empty_hover);
		ImageLinearLayout dragInfoLayout = (ImageLinearLayout) dragInfo;
		this.sourceId = dragInfoLayout.mCellNumber;

	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {

	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(0);

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
	 * Call this view's onClick listener. Return true if it was called. Clicks
	 * are ignored if the cell is empty.
	 *
	 * @return boolean
	 */

	@Override
	public void setDragController(DragController dragger) {
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
}
