package com.blahti.example.drag3;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private int RIGHT_IMAGECELL_INDEX = 1;
	private DragController mDragController;
	private int erapsedCount;

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
		mDragController = dragger;
	}

	private void setGridView() {
		// mDragController.removeAllDropTargets();
		int numLayoutChildren = mGrid.getChildCount();
		for (int i = 0; i < numLayoutChildren; i++) {
			ImageLinearLayout layout = (ImageLinearLayout) mGrid.getChildAt(i);
			int numVisibleChildren = layout.getChildCount();
			for (int imgCellLoopIdx = 0; imgCellLoopIdx < numVisibleChildren; imgCellLoopIdx++) {

				// 左ImageCellと右ImageCellオブジェクトをターゲットにセット
				LinearLayout parentView = (LinearLayout) layout.getChildAt(0);
				if (parentView.getChildAt(imgCellLoopIdx) instanceof ImageCell) {
					DropTarget view = (DropTarget) parentView
							.getChildAt(imgCellLoopIdx);
					mDragController.addDropTarget(view);
				}
			}
		}
	}

	/**
	 * onDropCompleted
	 *
	 */

	public void onDropCompleted(View target, boolean success) {
		// If the drop succeeds, the image has moved elsewhere.
		// So clear the image cell.

		if (success) {
			stopScroll();
			// ImageView sourceView = (ImageView) target;
			ImageCell sourceImageCell = this;

			ImageCell targetImageCell = (ImageCell) target;
			ImageLinearLayout parentTargetView = (ImageLinearLayout) target
					.getParent().getParent();
			Drawable d = targetImageCell.getDrawable();
			if (d != null) {

				// 格納ブックオブジェクトの画像パスを交換スタート

				// 元ブック
				BookImage sourceBook = this.mAdapter.mImageList
						.get(this.sourceId);

				// ターゲットブック
				BookImage targetBook = this.mAdapter.mImageList
						.get(parentTargetView.mCellNumber);

				String sourceImagePath = null; // 元画像パス
				String targetImagePath = null; // ターゲット画像パス
				Log.i("SourceBoook", sourceBook.toString());
				Log.i("targetBook", targetBook.toString());

				// 元ブック画像が左の場合
				if (sourceImageCell.findViewWithTag("LEFT_PAGE") != null) {
					sourceImagePath = sourceBook.getLeftImagePath();

					// ターゲットブック画像が左の場合
					if (targetImageCell.findViewWithTag("LEFT_PAGE") != null) {
						targetImagePath = targetBook.getLeftImagePath();
						sourceBook.setLeftImagePath(targetImagePath);
						targetBook.setLeftImagePath(sourceImagePath);

					} else if (targetImageCell.findViewWithTag("RIGHT_PAGE") != null) {
						// ターゲットブック画像が右の場合
						targetImagePath = targetBook.getRightImagePath();
						sourceBook.setLeftImagePath(targetImagePath);
						targetBook.setRightImagePath(sourceImagePath);
					}

				} else if (sourceImageCell.findViewWithTag("RIGHT_PAGE") != null) {
					// 元ブック画像が右の場合
					sourceImagePath = sourceBook.getRightImagePath();

					// ターゲットブック画像が左の場合
					if (targetImageCell.findViewWithTag("LEFT_PAGE") != null) {
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
	 * ドラッグビューがターゲットに落とされる
	 *
	 * @param source
	 *            ドラッグ開始されたオブジェクト
	 * @param x
	 *            ドロップされたx座標
	 * @param y
	 *            ドロップされたy座標
	 * @param xOffset
	 * @param yOffset
	 * @param dragView
	 *            ドラッグされているビュー
	 * @param dragInfo
	 *
	 */
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// Mark the cell so it is no longer empty.
		mEmpty = false;
		// int bg = mEmpty ? R.color.cell_empty : R.color.cell_filled;
		// setBackgroundResource(bg);
		// The view being dragged does not actually change its parent and switch
		// over to the ImageCell.
		// What we do is copy the drawable from the source view.
		ImageView sourceView = (ImageView) source;
		Drawable d = sourceView.getDrawable();
		if (d != null) {
			this.setImageDrawable(d);

		}
		stopScroll();
		toast("onDrop cell " + mCellNumber);

	}

	/**
	 *
	 * ドロップスポットにドラッグビューが乗ったとき
	 */
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo, int xMove,
			int yMove) {
		int bg = mEmpty ? R.color.cell_empty_hover : R.color.cell_filled_hover;
		setBackgroundResource(bg);
		ImageCell dragInfoImageCell = (ImageCell) dragInfo;
		ImageLinearLayout dragInfoLayout = (ImageLinearLayout) dragInfoImageCell
				.getParent().getParent();
		this.sourceId = dragInfoLayout.mCellNumber;
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo, int xMove,
			int yMove) {
		// 画面端ならスクロール
		if (yMove - ((View) dragView).getHeight() + 20 < 0) {
//			Log.i("DragInfo", String.valueOf(((View) dragView).getHeight() / 2));
			isLoop = true;
			ishighLoop = true;
			startPrevScroll();

			mDragController.setErapsedCount();
		} else if (yMove + ((View) dragView).getHeight() + 20 > getRootView()
				.getHeight()) {
//			Log.i("DragInfo", String.valueOf(((View) dragView).getHeight() / 2));
			isLoop = true;
			ishighLoop = true;
			startNextScroll();

			mDragController.setErapsedCount();
		} else {
			stopScroll();
		}
	}

	/**
	 * ドラッグが終了したとき
	 */
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo, int xMove,
			int yMove) {
		setBackgroundResource(0);

		// 画面端ならスクロール
		if (yMove - ((View) dragView).getHeight() + 20 < 0) {
//			Log.i("DragInfo", String.valueOf(((View) dragView).getHeight() / 2));
			isLoop = true;
			ishighLoop = true;
			startPrevScroll();

			mDragController.setErapsedCount();
		} else if (yMove + ((View) dragView).getHeight() + 20 > getRootView()
				.getHeight()) {
//			Log.i("DragInfo", String.valueOf(((View) dragView).getHeight() / 2));
			isLoop = true;
			ishighLoop = true;
			startNextScroll();

			mDragController.setErapsedCount();
		} else {
			stopScroll();
		}

	}

	/**
	 *
	 * @param source
	 *            ドラッグ開始されたオブジェクト
	 * @param x
	 *            ドロップされたx座標
	 * @param y
	 *            ドロップされたy座標
	 * @param xOffset
	 * @param yOffset
	 * @param dragView
	 *            ドラッグされているビュー
	 * @param dragInfo
	 * @return ドロップ条件が合えばtrue
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

	/**
	 * リストビューの取得
	 *
	 * @return
	 */
	private AbsListView getGridView() {
		return mGrid;
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
} // end ImageCell
