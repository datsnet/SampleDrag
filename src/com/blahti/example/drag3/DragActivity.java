package com.blahti.example.drag3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
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

	public static final boolean Debugging = true; // Use this to see extra
													// toast messages.
	public List<BookImage> bookImage;
	public ImageCellAdapter mAdapter;

	/**
 */
	// Methods

	/**
	 * Handle a click on a view.
	 *
	 */

	public void onClick(View v) {
		toast("Press and hold to drag an image.");

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
//			boolean imageChk = loadFromSDCard();
			boolean imageChk = loadFromSelectedPath("/mnt/sdcard/sample-image");
			if (imageChk) {
				mAdapter = new ImageCellAdapter(this, R.layout.demo,
						bookImage);
				gridView.setAdapter(mAdapter);
			} else if (!imageChk) {
				Toast.makeText(getApplicationContext(), "画像が読み込めませんでした",
						Toast.LENGTH_LONG).show();
			}
		}

		mDragController = new DragController(this);
		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mDragLayer.setDragController(mDragController);
		mDragLayer.setGridView(gridView);

		mDragController.setDragListener(mDragLayer);
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
		// trace("onLongClick in view: " + v);

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
		if (v instanceof LinearLayout) {
			ImageLinearLayout parent = (ImageLinearLayout)v.getParent();
			return startDrag(parent);
		}


		return startDrag(v);
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
	private boolean loadFromSDCard() {
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor c = this.managedQuery(uri, null, null, null, null);
		try {
			Log.i("COUNT", String.valueOf(c.getCount()));
			c.moveToFirst();

			while (c.getCount() > 0 && !c.isLast()) {
				// Log.i(TAG, "ID = " +
				// c.getString(c.getColumnIndexOrThrow("_id")));
				// Log.i(TAG, "ID = " +
				// c.getString(c.getColumnIndexOrThrow("_data")));

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
			return true;
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean loadFromSelectedPath(String path) {
//		private List<string> imgList = new ArrayList<string>();   // 画像PATH格納用
        // 外部ストレージ(SDカード)のデレクトリPATHを取得
        File file = new File(path);

        String imageFiles[] = file.list();
        // 指定ディレクトリ内のファイル検索
        int i = 0;
		while (imageFiles.length > i) {

			BookImage bImage = new BookImage();

			bImage.setLeftImagePath(path + "/" + imageFiles[i]);
			if (imageFiles.length > i + 1) {
				bImage.setRightImagePath(path + "/" + imageFiles[++i]);
			} else {
				Log.i("ttt", imageFiles[i]);
			}

			this.bookImage.add(bImage);
			i++;
		}


		return true;

	}
} // end class
