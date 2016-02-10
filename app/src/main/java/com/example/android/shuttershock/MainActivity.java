/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.shuttershock;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mfilterTitles;
	Bitmap bmp2;
	private boolean picTaken = false;
	ArrayList<Contact> imageArry = new ArrayList<Contact>();
	ContactImageAdapter adapter;
	// TextView textView;

	int i = 12;
	private static final int SELECT_PICTURE = 1; // intent code to select a picture
	String picPathData = "";

	ListView dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// DataBaseHandler db = new DataBaseHandler(this);
		// db.deleteAll();
		// db.close();
		readContacts();
		// textView = (TextView)findViewById(R.id.textView);

		adapter = new ContactImageAdapter(this, R.layout.screen_list, imageArry);
		dataList = (ListView) findViewById(R.id.list);
		dataList.setAdapter(adapter);

		mTitle = mDrawerTitle = getTitle();
		mfilterTitles = getResources().getStringArray(R.array.filters_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mfilterTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	// //////////////////////////////////////////////////////
	// When button is pressed it calls takePic to take the //
	//               picture using an intent               //
	// /////////////////////////////////////////////////////
	private void takePic() {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, 2);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2) {
			// Converts photo taken into a bitmap
			Bitmap photo = (Bitmap) data.getExtras().get("data");

			// Creates a new instance of a database
			DataBaseHandler db = new DataBaseHandler(this);

			String randomStringForPic = "";
			int length = 12;
			randomString(length);
			randomStringForPic = randomString(length);
			picPathData = randomStringForPic;

			// Compresses the bitmap into a byte array,
			// which the database can read as a BLOB
			/*
			 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
			 * photo.compress(Bitmap.CompressFormat.JPEG, 100, stream); byte
			 * imageInByte[] = stream.toByteArray();
			 */

			// Adds the picture into the contact class
			// db.addContact(new Contact(picPathData);
			String y = makeDate();
			Contact contact = new Contact(picPathData, y);
			db.addContact(contact);

			db.close();

			/*
			 * //Saves the image to the user's SD card File sdCardDirectory =
			 * Environment.getExternalStorageDirectory(); File image = new
			 * File(sdCardDirectory, picPathData + ".png");
			 * 
			 * Uri imageUri = Uri.fromFile(image);
			 */

			// Create Folder
			File folder = new File(Environment.getExternalStorageDirectory().toString() + "/ShutterShockFolder");
			folder.mkdirs();

			// Save the path as a string value
			String extStorageDirectory = folder.toString();

			// Create New file and name it Image2.PNG
			File image = new File(extStorageDirectory, picPathData + ".PNG");

			Uri imageUri = Uri.fromFile(image);

			// Send a broadcast so that the image that was just taken is saved
			// to the users SD card
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));

			boolean success = false;

			FileOutputStream outStream;
			try {

				// COnverts the image into a smaller form while still trying to
				// keep the quality of the image
				outStream = new FileOutputStream(image);
				photo.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				/* 100 to keep full quality of the image */

				outStream.flush();
				outStream.close();
				success = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// If image is saved tell the user and if not then tell the user
			if (success) {
				Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG).show();
				picTaken = true;
			} else {
				Toast.makeText(getApplicationContext(), "Photo not saved", Toast.LENGTH_LONG).show();
			}

			readContacts();
			// textView = (TextView)findViewById(R.id.textView);

			dataList.setAdapter(adapter);

		} else if (requestCode == SELECT_PICTURE) {

			if (data != null && resultCode == RESULT_OK) {

				Uri selectedImage = data.getData();

				if (selectedImage == null) {
					Log.d("Status", "data is null");
				} else {
					Log.d("Status", "data is not null");
				}
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				if (bmp2 != null && !bmp2.isRecycled()) {
					bmp2 = null;
				}

				Log.d("Status:", filePath);
				// textView.setText(filePath);
				bmp2 = BitmapFactory.decodeFile(filePath);
				// ivGalImg.setBackgroundResource(0);
				// ivGalImg.setImageBitmap(bmp);
				DataBaseHandler db = new DataBaseHandler(this);

				String randomStringForPic = "";
				int length = 12;
				randomString(length);
				randomStringForPic = randomString(length);
				picPathData = randomStringForPic;

				// Compresses the bitmap into a byte array,
				// which the database can read as a BLOB
				/*
				 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
				 * photo.compress(Bitmap.CompressFormat.JPEG, 100, stream); byte
				 * imageInByte[] = stream.toByteArray();
				 */

				// Adds the picture into the contact class
				// db.addContact(new Contact(picPathData);
				String y = makeDate();
				Contact contact = new Contact(picPathData, y);
				db.addContact(contact);

				db.close();

				// Saves the image to the user's SD card
				/*
				 * File sdCardDirectory =
				 * Environment.getExternalStorageDirectory(); File image = new
				 * File(sdCardDirectory, picPathData + ".png");
				 * 
				 * Uri imageUri = Uri.fromFile(image);
				 */
				// Create Folder
				File folder = new File(Environment.getExternalStorageDirectory().toString() + "/ShutterShockFolder");
				folder.mkdirs();

				// Save the path as a string value
				String extStorageDirectory = folder.toString();

				// Create New file and name it Image2.PNG
				File image = new File(extStorageDirectory, picPathData + ".PNG");

				Uri imageUri = Uri.fromFile(image);

				// Send a broadcast so that the image that was just taken is
				// saved to the users SD card
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));

				boolean success = false;

				FileOutputStream outStream;
				try {

					// COnverts the image into a smaller form while still trying
					// to keep the quality of the image
					outStream = new FileOutputStream(image);
					bmp2.compress(Bitmap.CompressFormat.PNG, 100, outStream);
					/* 100 to keep full quality of the image */

					outStream.flush();
					outStream.close();
					success = true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// If image is saved tell the user and if not then tell the user
				if (success) {
					Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG).show();
					picTaken = true;
				} else {
					Toast.makeText(getApplicationContext(), "Photo not saved", Toast.LENGTH_LONG).show();
				}

				/*
				 * //Compresses the bitmap into a byte array, //which the
				 * database can read as a BLOB if (bmp2 != null) {
				 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
				 * bmp2.compress(Bitmap.CompressFormat.JPEG, 100, stream); byte
				 * imageInByte[] = stream.toByteArray();
				 */

				/*
				 * I'm going to finish this after pic taken is fixed //Adds the
				 * picture into the contact class db.addContact(new Contact(i,
				 * "gallery picture" + "", imageInByte)); i++;
				 */
				readContacts();
				// textView = (TextView)findViewById(R.id.textView);

				dataList.setAdapter(adapter);
			}
		}

	}

	public void readContacts() {
		imageArry.clear();
		DataBaseHandler db = new DataBaseHandler(this);
		List<Contact> contacts = db.getAllContacts();
		for (Contact cn : contacts) {
			String log = "ID:" + cn.getID() + " ,Image: " + cn.getImage();

			// Writing Contacts to log
			Log.d("Result: ", log);
			// add contacts data in arrayList
			imageArry.add(cn);

		}
		db.close();
	}

	public void readDates() {
		imageArry.clear();
		DataBaseHandler db = new DataBaseHandler(this);
		List<Contact> contacts = db.getContactsByDate();
		for (Contact cn : contacts) {
			String log = "ID:" + cn.getID() + " ,Image: " + cn.getImage();

			// Writing Contacts to log
			Log.d("Result: ", log);
			// add contacts data in arrayList
			imageArry.add(cn);

		}
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.take_pic:
			Toast.makeText(getApplicationContext(), "button is working", Toast.LENGTH_LONG).show();
			takePic();
			// galleryAddPic();
			return true;
		case R.id.upload:
			/*
			 * Intent intentx = new Intent(); intentx.setType("image/*");
			 * intentx.setAction(Intent.ACTION_GET_CONTENT);
			 * startActivityForResult( Intent.createChooser(intentx,
			 * "Select Picture"), SELECT_PICTURE);
			 */

			final Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			startActivityForResult(galleryIntent, SELECT_PICTURE);
			// galleryAddPic();
			return true;
		case R.id.look:
			LongOperation longOperation = new LongOperation(this);
			longOperation.execute();
			// readContacts();
			return true;
		case R.id.action_websearch:
			// create intent to perform web search for this filter
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
			// catch event that there's no activity to handle intent
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new filterFragment();
        Bundle args = new Bundle();
        args.putInt(filterFragment.ARG_filter_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mfilterTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

        
        switch (mfilterTitles[position]) {
        case Date:
        	//TODO: ascending vs descending ??
        	readDates();
        	adapter = new ContactImageAdapter(this, R.layout.screen_list,
                    imageArry);
            dataList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        	break;
        	/*
        	 //create new array that holds the values of the original array
        	newImageArray = new ArrayList<String>(Arrays.asList(mfilterTitles));
            
        	//sort the array in order
        	Collections.sort(newImageArray);
            
            //create new instance of adapter and set the adapter 
            adapter = new ContactImageAdapter(this, R.layout.screen_list,
                    newImageArray);
            dataList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            */
        case Location:
        	/*TODO: JB -- retrieve location data and organize imageArry 
        	 * to reflect such data. create the new instance of the adapter
        	 * and set it to the datalist (for viewing) 
        	 */
        	break;
        case Album:
        	//TODO: NS -- get album data, sort by name, set adapter
        	break;
        case File Size:
            //TODO: Unknown -- get file size, sort in numerical order, set adaptrt
        	break;
         default:
        	  //TODO: return error?
        	 break;
         }
    }

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Fragment that appears in the "content_frame", shows a filter
	 */
	public static class filterFragment extends Fragment {
		public static final String ARG_filter_NUMBER = "filter_number";

		public filterFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
			int i = getArguments().getInt(ARG_filter_NUMBER);
			String filter = getResources().getStringArray(R.array.filters_array)[i];

			int imageId = getResources().getIdentifier(filter.toLowerCase(Locale.getDefault()), "drawable", getActivity().getPackageName());
			((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
			getActivity().setTitle(filter);
			return rootView;
		}
	}

	public class LongOperation extends AsyncTask<Void, Void, String> {

		public LongOperation(Context context) {

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(Void... params) {
			readContacts();
			Log.d("Param", "doing in background");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("Param", "Finished");
		}
	}

	public String randomString(int length) {

		// Generates a random name for the image that the user has taken

		// THis is the alphabet that I will be using for the name
		final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random rnd = new Random();

		// Use a string buiolder to build a sequence of random numbers based on
		// the length that is passed in the parameter
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public String makeDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// get current date time with Date()
		Date date = new Date();
		String x = dateFormat.format(date).toString();

		return x;
	}
}
