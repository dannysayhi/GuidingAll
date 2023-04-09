package org.wood.guidingall.constructor;

import android.content.Context;
import android.widget.Toast;

public class MyToast {
	private Context context;
	private Toast mToast;

	public MyToast(Context context) {
		this.context = context;
	};

	public void show(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		}

		mToast.setText(text);
		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.show();
	}

}
