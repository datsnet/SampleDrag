package com.blahti.example.drag3;

import java.util.ArrayList;
import java.util.List;

import com.blahti.example.drag3.R;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

/**
 * This activity presents a screen with a grid on which images can be added and
 * moved around. It also defines areas on the screen where the dragged views can
 * be dropped. Feedback is provided to the user as the objects are dragged over
 * these drop zones.
 *
 * <p>
 * Like the DragActivity in the previous version of the DragView example
 * application, the code here is derived from the Android Launcher code.
 *
 */

public class DragActivity extends Activity implements View.OnLongClickListener,
		View.OnClickListener // , AdapterView.OnItemClickListener
{

	/**
 */
	// Constants

	private static final int HIDE_TRASHCAN_MENU_ID = Menu.FIRST;
	private static final int SHOW_TRASHCAN_MENU_ID = Menu.FIRST + 1;
	private static final int ADD_OBJECT_MENU_ID = Menu.FIRST + 2;

	/**
 */
	// Variables

	private DragController mDragController; // Object that handles a drag-drop
											// sequence. It intersacts with
											// DragSource and DropTarget
											// objects.
	private DragLayer mDragLayer; // The ViewGroup within which an object can be
									// dragged.
	private DeleteZone mDeleteZone; // A drop target that is used to remove
									// objects from the screen.
	private int mImageCount = 0; // The number of images that have been added to
									// screen.
	private ImageCell mLastNewCell = null; // The last ImageCell added to the
											// screen when Add Image is clicked.

	public static final boolean Debugging = false; // Use this to see extra
													// toast messages.
	private List<BookImage> bookImage;

	/**
 */
	// Methods

	/**
	 * Add a new image so the user can move it around. It shows up in the
	 * image_source_frame part of the screen.
	 *
	 */

	public void addNewImageToScreen(int resourceId) {
		if (mLastNewCell != null)
			mLastNewCell.setVisibility(View.GONE);

		FrameLayout imageHolder = (FrameLayout) findViewById(R.id.image_source_frame);
		if (imageHolder != null) {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
					Gravity.CENTER);
			ImageCell newView = new ImageCell(this);
			newView.setImageResource(resourceId);
			imageHolder.addView(newView, lp);
			newView.mEmpty = false;
			newView.mCellNumber = -1;
			newView.setOnClickListener(this);
			newView.setOnLongClickListener(this);
			mLastNewCell = newView;
			mImageCount++;
		}
	}

	/**
	 * Add one of the images to the screen so the user has a new image to move
	 * around. See addImageToScreen.
	 *
	 */

	public void addNewImageToScreen() {
		int resourceId = R.drawable.hello;

		int m = mImageCount % 3;
		if (m == 1)
			resourceId = R.drawable.photo1;
		else if (m == 2)
			resourceId = R.drawable.photo2;
		addNewImageToScreen(resourceId);
	}

	/**
	 * Handle a click on a view.
	 *
	 */

	public void onClick(View v) {
		toast("Press and hold to drag an image.");
	}

	/**
	 * Handle a click of the Add Image button
	 *
	 */

	public void onClickAddImage(View v) {
		addNewImageToScreen();
	}

	/**
	 * onCreate - called when the activity is first created.
	 *
	 * Creates a drag controller and sets up three views so click and long click
	 * on the views are sent to this activity. The onLongClick method starts a
	 * drag sequence.
	 *
	 */

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.demo);

		GridView gridView = (GridView) findViewById(R.id.image_grid_view);

		if (gridView == null)
			toast("Unable to find GridView");
		else {
			bookImage = new ArrayList<BookImage>();
			// SDカードに入っているデータを取得
			loadFromSDCard();

			gridView.setAdapter(new ImageCellAdapter(this, R.layout.demo,
					bookImage));
			// gridView.setOnItemClickListener (this);
		}

		mDragController = new DragController(this);
		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mDragLayer.setDragController(mDragController);
		mDragLayer.setGridView(gridView);

		mDragController.setDragListener(mDragLayer);
//		 mDragController.addDropTarget (mDragLayer);

		mDeleteZone = (DeleteZone) findViewById(R.id.delete_zone_view);

		// Give the user a little guidance.
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.instructions),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Build a menu for the activity.
	 *
	 */

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, HIDE_TRASHCAN_MENU_ID, 0, "Hide Trashcan").setShortcut('1',
				'c');
		menu.add(0, SHOW_TRASHCAN_MENU_ID, 0, "Show Trashcan").setShortcut('2',
				'c');
		menu.add(0, ADD_OBJECT_MENU_ID, 0, "Add View").setShortcut('9', 'z');

		return true;
	}

	/**
	 * Handle a click of an item in the grid of cells.
	 *
	 */

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ImageCell i = (ImageCell) v;
		trace("onItemClick in view: " + i.mCellNumber);
	}

	/**
	 * Handle a long click.
	 *
	 * @param v
	 *            View
	 * @return boolean - true indicates that the event was handled
	 */

	public boolean onLongClick(View v) {
		trace("onLongClick in view: " + v);

		// Make sure the drag was started by a long press as opposed to a long
		// click.
		// (Note: I got this from the Workspace object in the Android Launcher
		// code.
		// I think it is here to ensure that the device is still in touch mode
		// as we start the drag operation.)
		if (!v.isInTouchMode()) {
			toast("isInTouchMode returned false. Try touching the view again.");
			return false;
		}
		return startDrag(v);
	}

	/**
	 * Perform an action in response to a menu item being clicked.
	 *
	 */

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case HIDE_TRASHCAN_MENU_ID:
			if (mDeleteZone != null)
				mDeleteZone.setVisibility(View.INVISIBLE);
			return true;
		case SHOW_TRASHCAN_MENU_ID:
			if (mDeleteZone != null)
				mDeleteZone.setVisibility(View.VISIBLE);
			return true;
		case ADD_OBJECT_MENU_ID:
			// Add a new object to the screen;
			addNewImageToScreen();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Start dragging a view.
	 *
	 */

	public boolean startDrag(View v) {
		DragSource dragSource = (DragSource) v;

		// We are starting a drag. Let the DragController handle it.
		mDragController.startDrag(v, dragSource, dragSource,
				DragController.DRAG_ACTION_MOVE);

		return true;
	}

	/**
	 * Show a string on the screen via Toast.
	 *
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

	/**
	 * Send a message to the debug log. Also display it using Toast if Debugging
	 * is true.
	 */

	public void trace(String msg) {
		Log.d("DragActivity", msg);
		if (!Debugging)
			return;
		toast(msg);
	}

	// SDカードに保存されている画像を取得
	private void loadFromSDCard() {
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor c = this.managedQuery(uri, null, null, null, null);

		Log.i("COUNT", String.valueOf(c.getCount()));
		c.moveToFirst();
		while (c.getCount() > 0 && !c.isLast()) {
//			Log.i(TAG, "ID = " + c.getString(c.getColumnIndexOrThrow("_id")));
//			Log.i(TAG, "ID = " + c.getString(c.getColumnIndexOrThrow("_data")));

			BookImage bImage = new BookImage();

			// 左画像URLを配列に格納
			bImage.setLeftImagePath(c.getString(c
					.getColumnIndexOrThrow("_data")));

			if (!c.isLast()) {
				c.moveToNext();
				// 右画像URLを配列に格納
				bImage.setRightImagePath(c.getString(c
						.getColumnIndexOrThrow("_data")));
				if (!c.isLast())
					c.moveToNext();
			}
			bookImage.add(bImage);
		}
	}
} // end class
