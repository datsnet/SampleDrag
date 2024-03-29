package com.blahti.example.drag3;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * This class is used with a GridView object. It provides a set of ImageCell
 * objects that support dragging and dropping.
 *
 */

public class ImageCellAdapter extends ArrayAdapter<BookImage> {

	// Variables
	public ViewGroup mParentView = null;
	private Context mContext;
//	private List<BookImage> mImageList;
	public List<BookImage> mImageList;
	private DragController mDragController; // Object that handles a drag-drop

	public ImageCellAdapter(Context context, int resource,
			List<BookImage> objects) {
		super(context, resource, objects);
		this.mContext = context;
		this.mImageList = objects;
	}

	/**
	 * getCount
	 */
	public int getCount() {
//		Resources res = mContext.getResources();
//		int numImages = res.getInteger(R.integer.num_images);
//		return numImages;
		return mImageList.size();
	}

	public BookImage getItem(int position) {
		return mImageList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * getView Return a view object for the grid.
	 *
	 * @return ImageCell
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		mParentView = parent;

		ImageLinearLayout layout = null;
		ImageCell leftImg = null;
		ImageCell rightImg = null;
		ImageLinearLayout centerView = null;

		if (convertView == null) {
//			layout = new ImageLinearLayout(mContext);
			layout = new ImageLinearLayout(mContext, (ImageCellAdapter)this);
			layout.setGravity(LinearLayout.HORIZONTAL);
			layout.setOnClickListener((View.OnClickListener) mContext);
			layout.setOnLongClickListener((View.OnLongClickListener) mContext);


			leftImg = new ImageCell(mContext, (ImageCellAdapter)this);
			// If it's not recycled, create a new ImageCell.
			leftImg.setLayoutParams(new GridView.LayoutParams(85, 85));
			leftImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
			leftImg.setPadding(8, 8, 8, 8);
			leftImg.setTag("LEFT_PAGE");
			// Set up to be a drop target and drag source.
			leftImg.setOnClickListener((View.OnClickListener) mContext);
			leftImg.setOnLongClickListener((View.OnLongClickListener) mContext);

			rightImg = new ImageCell(mContext);
			// If it's not recycled, create a new ImageCell.
			rightImg.setLayoutParams(new GridView.LayoutParams(85, 85));
			rightImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
			rightImg.setPadding(8, 8, 8, 8);
			rightImg.setTag("RIGHT_PAGE");
			// Set up to be a drop target and drag source.
			rightImg.setOnClickListener((View.OnClickListener) mContext);
			rightImg.setOnLongClickListener((View.OnLongClickListener) mContext);


			// 真ん中に両方選択する用の判定Layout追加
			centerView = new ImageLinearLayout(mContext);
			centerView.setGravity(LinearLayout.HORIZONTAL);
			centerView.setTag("CENTER_PAGE");
			LinearLayout.LayoutParams layoutParams =
		              new LinearLayout.LayoutParams(20, LinearLayout.LayoutParams.WRAP_CONTENT);
//			layoutParams.setMargins(5, 10, 5, 10);
			centerView.setLayoutParams(layoutParams);
			centerView.setOnClickListener((View.OnClickListener) mContext);
			centerView.setOnLongClickListener((View.OnLongClickListener) mContext);


			layout.addView(leftImg);
			layout.addView(centerView);
			layout.addView(rightImg);
		} else {
			layout = (ImageLinearLayout) convertView;
			leftImg = (ImageCell) layout.findViewWithTag("LEFT_PAGE");
			rightImg = (ImageCell) layout.findViewWithTag("RIGHT_PAGE");
			centerView = (ImageLinearLayout)layout.findViewWithTag("CENTER_PAGE");
		}

		layout.mCellNumber = position;
		layout.mGrid = (GridView) mParentView;

		leftImg.mCellNumber = position;
		leftImg.mGrid = (GridView) mParentView;
		leftImg.mEmpty = true;



		rightImg.mCellNumber = position;
		rightImg.mGrid = (GridView) mParentView;
		rightImg.mEmpty = true;

		// 両方選択用
		layout.mCellNumber = position;
		layout.mGrid = (GridView) mParentView;
		layout.mEmpty = true;

		// ビットマップ変換
		BitmapFactory.Options bmpOp = new BitmapFactory.Options();
		bmpOp.inSampleSize = 2;

		BookImage bookImage = mImageList.get(position);
		leftImg.setImageBitmap(BitmapFactory.decodeFile(
				bookImage.getLeftImagePath(), bmpOp));
		if (bookImage.getRightImagePath() != null) {
			rightImg.setImageBitmap(BitmapFactory.decodeFile(
					bookImage.getRightImagePath(), bmpOp));
		}

		Log.i("getView", String.valueOf(position));

		return layout;
	}

	public void onClick(View v) {

	}

	public boolean onLongClick(View v) {
		// Make sure the drag was started by a long press as opposed to a long
		// click.
		// (Note: I got this from the Workspace object in the Android Launcher
		// code.
		// I think it is here to ensure that the device is still in touch mode
		// as we start the drag operation.)
		if (!v.isInTouchMode()) {
			return false;
		}
		return startDrag(v);
	}

	public boolean startDrag(View v) {
		DragSource dragSource = (DragSource) v;

		// We are starting a drag. Let the DragController handle it.
		mDragController.startDrag(v, dragSource, dragSource,
				DragController.DRAG_ACTION_MOVE);

		return true;
	}

	public boolean setBookList(int prePosition, int posPosition) {
		return false;
	}
	public void setFresh() {
		this.notifyDataSetChanged();
	}
}
