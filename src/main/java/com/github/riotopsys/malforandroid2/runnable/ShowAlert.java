package com.github.riotopsys.malforandroid2.runnable;

import android.app.AlertDialog;
import android.content.Context;

public class ShowAlert implements Runnable {

	private Context ctx;
	private String title;
	private String msg;

	public ShowAlert(Context ctx, String title, String msg) {
		this.ctx = ctx;
		this.title = title;
		this.msg = msg;
	}

	@Override
	public void run() {
		new AlertDialog.Builder(ctx).setTitle(title)
				.setMessage(msg).create()
				.show();
	}

}
