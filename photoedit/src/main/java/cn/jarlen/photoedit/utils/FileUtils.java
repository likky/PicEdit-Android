/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.jarlen.photoedit.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;

/**
 * @author jarlen
 */
public class FileUtils
{

	public static String SDCARD_PAHT = Environment
			.getExternalStorageDirectory().getPath();

	public static String DCIMCamera_PATH = Environment
			.getExternalStorageDirectory() + "/DCIM/Camera/";

	/**
	 * Check if sdcard is available
	 *
	 * @return true is available; false is not available
	 */
	public static boolean isSDAvailable()
	{
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return true;
	}

	public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth, int newHeight)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap.recycle();
		return resizedBitmap;
	}
	
	public static Bitmap ResizeBitmap(Bitmap bitmap, int scale)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(1/scale, 1/scale);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap.recycle();
		return resizedBitmap;
	}

	public static String getNewFileName()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());

		return formatter.format(curDate);
	}

	/**
	 * 
	 * @param context
	 * Context
	 *
	 * @param bm
	 * Bitmap to be saved
	 *
	 * @param name
	 * Save the name can be null, according to the time to customize a file name
	 *
	 * @return to ".jpg" format to the album
	 * 
	 */
	public static Boolean saveBitmapToCamera(Context context, Bitmap bm,
			String name)
	{

		File file = null;

		if (name == null || name.equals(""))
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date curDate = new Date(System.currentTimeMillis());
			name = formatter.format(curDate) + ".jpg";
		}

		file = new File(DCIMCamera_PATH, name);
		if (file.exists())
		{
			file.delete();
		}

		try
		{
			FileOutputStream out = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;

		} catch (IOException e)
		{

			e.printStackTrace();
			return false;
		}

		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		context.sendBroadcast(intent);

		return true;
	}

	/**
	 * 
	 * @param bitmap
	 * @param destPath
	 * @param quality
	 */
	public static void writeImage(Bitmap bitmap, String destPath, int quality)
	{
		try
		{
			deleteFile(destPath);
			if (createFile(destPath))
			{
				FileOutputStream out = new FileOutputStream(destPath);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out))
				{
					out.flush();
					out.close();
					out = null;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean createFile(String filePath)
	{
		try
		{
			File file = new File(filePath);
			if (!file.exists())
			{
				if (!file.getParentFile().exists())
				{
					file.getParentFile().mkdirs();
				}

				return file.createNewFile();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}

	public static File createNewFile(String filePath) {

		File file = new File(filePath);
		if (!file.exists())
        {
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }

            return file;
        }
		return file;
	}

	/**
	 * delete
	 * 
	 * @param filePath
	 *
	 * @return true if this file was deleted, false otherwise
	 */
	public static boolean deleteFile(String filePath)
	{
		try
		{
			File file = new File(filePath);
			if (file.exists())
			{
				return file.delete();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Delete all the files in the dir directory, including deleting the deleted folders
	 * 
	 * @param dir
	 */
	public static void deleteDirectory(File dir)
	{
		if (dir.isDirectory())
		{
			File[] listFiles = dir.listFiles();
			for (int i = 0; i < listFiles.length; i++)
			{
				deleteDirectory(listFiles[i]);
			}
		}
		dir.delete();
	}

}
