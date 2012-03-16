/*
 * This is a modified version of a class from the Android Open Source Project.
 * The original copyright and license information follows.
 *
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blahti.example.drag3;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * A ViewGroup that supports dragging within it. Dragging starts in an object
 * that implements the DragSource interface and ends in an object that
 * implements the DropTarget interface.
 *
 * ドラッグ操作をサポートする大元カスタムFrameLAyoutクラス
 * DragSourceからドラッグが始まり、DragTargetで終了
 *
 */
public class DragLayer extends FrameLayout implements
		DragController.DragListener {
	DragController mDragController;
	GridView mGridView;
	List<BookImage> mImageList;

	public DragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDragController(DragController controller) {
		mDragController = controller;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return mDragController.dispatchKeyEvent(event)
				|| super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mDragController.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return mDragController.onTouchEvent(ev);
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		return mDragController.dispatchUnhandledMove(focused, direction);
	}

	/**
	 * GridViewを返す
	 *
	 * @return GridView
	 */

	public GridView getGridView() {
		// if (mGridView == null) {}
		return mGridView;
	} // end getGridView

	/**
	 * GridViewをセット
	 *
	 * @param newValue
	 *            GridView
	 */

	public void setGridView(GridView newValue) {
		mGridView = newValue;
	} // end setGridView
	/* end Property GridView */

	/**
 */
	// DragListener Interface Methods

	/**
	 * ドラッグ開始
	 *
	 * @param source
	 *            An object representing where the drag originated
	 * @param info
	 *            The data associated with the object that is being dragged
	 * @param dragAction
	 *            The drag action: either
	 *            {@link DragController#DRAG_ACTION_MOVE} or
	 *            {@link DragController#DRAG_ACTION_COPY}
	 */

	public void onDragStart(DragSource source, Object info, int dragAction) {
		// We are starting a drag.
		// Build up a list of DropTargets from the child views of the GridView.
		// Tell the drag controller about them.

		if (mGridView != null) {
			if (source instanceof ImageCell) {
				int numLayoutChildren = mGridView.getChildCount();
				for (int i = 0; i < numLayoutChildren; i++) {
					ImageLinearLayout layout = (ImageLinearLayout) mGridView
							.getChildAt(i);
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

//				int numLayoutChildren = mGridView.getCount();
//				for (int i = 0; i < numLayoutChildren; i++) {
//					ImageLinearLayout layout = (ImageLinearLayout) mGridView
//							.getItemAtPosition(i);
//					int numVisibleChildren = layout.getChildCount();
//					for (int imgCellLoopIdx = 0; imgCellLoopIdx < numVisibleChildren; imgCellLoopIdx++) {
//
//						// 左ImageCellと右ImageCellオブジェクトをターゲットにセット
//						LinearLayout parentView = (LinearLayout) layout
//								.getChildAt(0);
//						if (parentView.getChildAt(imgCellLoopIdx) instanceof ImageCell) {
//							DropTarget view = (DropTarget) parentView
//									.getChildAt(imgCellLoopIdx);
//							mDragController.addDropTarget(view);
//						}
//					}
//				}

			} else if (source instanceof ImageLinearLayout) {
				int numVisibleChildren = mGridView.getChildCount();
				for (int i = 0; i < numVisibleChildren; i++) {
					DropTarget view = (DropTarget) mGridView.getChildAt(i);
					mDragController.addDropTarget(view);
				}
			}
		}

	}

	/**
	 * ドラッグドロップ操作終了
	 */

	public void onDragEnd() {
		mDragController.removeAllDropTargets();
	}

	/**
 */
	// Other Methods


	public void setTargetsinGrid(Object source) {
//		mDragController.removeAllDropTargets();
		if (source instanceof ImageCell) {
			int numLayoutChildren = mGridView.getChildCount();
			for (int i = 0; i < numLayoutChildren; i++) {
				ImageLinearLayout layout = (ImageLinearLayout) mGridView
						.getChildAt(i);
				int numVisibleChildren = layout.getChildCount();
				for (int imgCellLoopIdx = 0; imgCellLoopIdx < numVisibleChildren; imgCellLoopIdx++) {

					// 左ImageCellと右ImageCellオブジェクトをターゲットにセット
					LinearLayout parentView = (LinearLayout) layout
							.getChildAt(0);
					if (parentView.getChildAt(imgCellLoopIdx) instanceof ImageCell) {
						DropTarget view = (DropTarget) parentView
								.getChildAt(imgCellLoopIdx);
						mDragController.addDropTarget(view);
					}
				}
			}
		} else if (source instanceof ImageLinearLayout) {
			int numVisibleChildren = mGridView.getChildCount();
			for (int i = 0; i < numVisibleChildren; i++) {
				DropTarget view = (DropTarget) mGridView.getChildAt(i);
				mDragController.addDropTarget(view);
			}
		}
	}


	/**
	 * Show a string on the screen via Toast.
	 *
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		// if (!DragActivity.Debugging) return;
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

} // end class
