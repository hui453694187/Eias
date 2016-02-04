package com.yunfang.eias.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.yunfang.eias.R;

public class LogoActivity extends Activity {
	private ImageView iv_logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.logo_activity);
		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		AlphaAnimation anim = new AlphaAnimation(0.5f, 1.0f);
		anim.setDuration(800);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				try {
					startActivity(new Intent(LogoActivity.this,
							LoginActivity.class));
					LogoActivity.this.finish();
				} catch (Exception e) {
					
				}

			}
		});
		iv_logo.setAnimation(anim);
	}

	@Override
	protected void onDestroy() {
		//BaseService.removeActivity(this);
		super.onDestroy();
	}
	
}
