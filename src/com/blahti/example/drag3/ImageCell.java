package com.blahti.example.drag3;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This subclass of ImageView is used to display an image on an GridView. An
 * ImageCell knows which cell on the grid it is showing and which grid it is
 * attached to Cell numbers are from 0 to NumCells-1. It also knows if it is
 * empty.
 *
 * <p>
 * Image cells are places where images can be dragged from and dropped onto.
 * Therefore, this class implements both the DragSource and DropTarget
 * interfaces.
 *
 */

public class ImageCell extends ImageView implements DragSource, DropTarget,
		Cloneable {
	/**
	 *
	 */
	public boolean mEmpty = true;
	public int mCellNumber = -1;
	public GridView mGrid;
	private Context mContext;
	private ImageCellAdapter mAdapter;
	private List<BookImage> mImageList;
	private int sourceId;
	private int LEFT_IMAGECELL_INDEX = 0;
	private int RIGHT_IMAGECELL_INDEX = 2;

	/**
	 * Constructors
	 */
	// Constructors

	public ImageCell(Context context) {
		super(context);
		this.mContext = context;
	}

	public ImageCell(Context context, ImageCellAdapter adapter) {
		super(context);
		this.mContext = context;
		this.mAdapter = adapter;
	}

	public ImageCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public ImageCell(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		mContext = context;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 */
	// DragSource interface methods

	/**
	 * setDragController
	 *
	 */

	public void setDragController(DragController dragger) {
	}

	/**
	 * onDropCompleted
	 *
	 */

	public void onDropCompleted(View target, boolean success) {
		// If the drop succeeds, the image has moved elsewhere.
		// So clear the image cell.
		if (success) {
			ImageView sourceView = (ImageView) target;
			ImageCell sourceImageCell = this;

			ImageCell targetImageCell = (ImageCell) target;
			ImageLinearLayout parentTargetView = (ImageLinearLayout) target.getParent();
			Drawable d = sourceView.getDrawable();
			if (d != null) {


				// 格納ブックオブジェクトの画像パスを交換スタート

				// 元ブック
				BookImage sourceBook = this.mAdapter.mImageList.get(this.sourceId);

				// ターゲットブック
				BookImage targetBook = this.mAdapter.mImageList.get(parentTargetView.mCellNumber);


				String sourceImagePath = null;	// 元画像パス
				String targetImagePath = null;	// ターゲット画像パス
				Log.i("SourceBoook", sourceBook.toString());
				Log.i("targetBook", targetBook.toString());


				// 元ブック画像が左の場合
				if (sourceImageCell.findViewWithTag("LEFT_PAGE") != null) {
					sourceImagePath = sourceBook.getLeftImagePath();


					// ターゲットブック画像が左の場合
					if (targetImageCell.findViewWithTag("LEFT_PAGE") != null ) {
						targetImagePath = targetBook.getLeftImagePath();
						sourceBook.setLeftImagePath(targetImagePath);
						targetBook.setLeftImagePath(sourceImagePath);

					} else if (targetImageCell.findViewWithTag("RIGHT_PAGE") != null) {
						// ターゲットブック画像が右の場合
						targetImagePath = targetBook.getRightImagePath();
						sourceBook.setLeftImagePath(targetImagePath);
						targetBook.setRightImagePath(sourceImagePath);
					}


				} else if (sourceImageCell.findViewWithTag("RIGHT_PAGE") != null){
					// 元ブック画像が右の場合
					sourceImagePath = sourceBook.getRightImagePath();

					// ターゲットブック画像が左の場合
					if (targetImageCell.findViewWithTag("LEFT_PAGE") != null ) {
						targetImagePath = targetBook.getLeftImagePath();
						sourceBook.setRightImagePath(targetImagePath);
						targetBook.setLeftImagePath(sourceImagePath);
					} else if (targetImageCell.findViewWithTag("RIGHT_PAGE") != null) {
						// ターゲットブック画像が右の場合
						targetImagePath = targetBook.getRightImagePath();
						sourceBook.setRightImagePath(targetImagePath);
						targetBook.setRightImagePath(sourceImagePath);
					}
				}
			}

			d = sourceImageCell.getDrawable();
			this.setImageDrawable(d);
			this.mAdapter.notifyDataSetChanged();
		}
	}

	/**
 */
	// DropTarget interface implementation

	/**
	 * Handle an object being dropped on the DropTarget. This is the where the
	 * drawable of the dragged view gets copied into the ImageCell.
	 *
	 * @param source
	 *            DragSource where the drag started
	 * @param x
	 *            X coordinate of the drop location
	 * @param y
	 *            Y coordinate of the drop location
	 * @param xOffset
	 *            Horizontal offset with the object being dragged where the
	 *            original touch happened
	 * @param yOffset
	 *            Vertical offset with the object being dragged where the
	 *            original touch happened
	 * @param dragView
	 *            The DragView that's being dragged around on screen.
	 * @param dragInfo
	 *            Data associated with the object being dragged
	 *
	 */
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// Mark the cell so it is no longer empty.

		mEmpty = false;
//		int bg = mEmpty ? R.color.cell_empty : R.color.cell_filled;
//		setBackgroundResource(bg);
		// The view being dragged does not actually change its parent and switch
		// over to the ImageCell.
		// What we do is copy the drawable from the source view.
		ImageView sourceView = (ImageView) source;
		Drawable d = sourceView.getDrawable();
		if (d != null) {
			this.setImageDrawable(d);

		}

		toast("onDrop cell " + mCellNumber);

	}

	/**
	 * React to a dragged object entering the area of this DropSpot. Provide the
	 * user with some visual feedback.
	 */
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		int bg = mEmpty ? R.color.cell_empty_hover : R.color.cell_filled_hover;
		setBackgroundResource(bg);
		ImageCell dragInfoImageCell = (ImageCell) dragInfo;
		ImageLinearLayout dragInfoLayout = (ImageLinearLayout) dragInfoImageCell.getParent();
		this.sourceId = dragInfoLayout.mCellNumber;
	}

	/**
	 * React to something being dragged over the drop target.
	 */
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	/**
	 * React to a drag
	 */
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(0);
	}

	/**
	 * Check if a drop action can occur at, or near, the requested location.
	 * This may be called repeatedly during a drag, so any calls should return
	 * quickly.
	 *
	 * @param source
	 *            DragSource where the drag started
	 * @param x
	 *            X coordinate of the drop location
	 * @param y
	 *            Y coordinate of the drop location
	 * @param xOffset
	 *            Horizontal offset with the object being dragged where the
	 *            original touch happened
	 * @param yOffset
	 *            Vertical offset with the object being dragged where the
	 *            original touch happened
	 * @param dragView
	 *            The DragView that's being dragged around on screen.
	 * @param dragInfo
	 *            Data associated with the object being dragged
	 * @return True if the drop will be accepted, false otherwise.
	 */
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// An ImageCell accepts a drop if it is empty and if it is part of a
		// grid.
		// A free-standing ImageCell does not accept drops.
		return true;
		// return mEmpty && (mCellNumber >= 0);
	}

	/**
	 * Estimate the surface area where this object would land if dropped at the
	 * given location.
	 *
	 * @param source
	 *            DragSource where the drag started
	 * @param x
	 *            X coordinate of the drop location
	 * @param y
	 *            Y coordinate of the drop location
	 * @param xOffset
	 *            Horizontal offset with the object being dragged where the
	 *            original touch happened
	 * @param yOffset
	 *            Vertical offset with the object being dragged where the
	 *            original touch happened
	 * @param dragView
	 *            The DragView that's being dragged around on screen.
	 * @param dragInfo
	 *            Data associated with the object being dragged
	 * @param recycle
	 *            {@link Rect} object to be possibly recycled.
	 * @return Estimated area that would be occupied if object was dropped at
	 *         the given location. Should return null if no estimate is found,
	 *         or if this target doesn't provide estimations.
	 */
	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		return null;
	}

	/**
 */
	// Other Methods

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

	/**
	 * Show a string on the screen via Toast if DragActivity.Debugging is true.
	 *
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		if (!DragActivity.Debugging)
			return;
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

} // end ImageCell
