package com.byd.vtdr2.widget;

//import com.byd.appstore.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import com.byd.vtdr2.R;
/**#
 * 该主题运用在可点击的字眼，点击时候具有发光的效果
 *
 */
public class ThemeLightButton extends LightButton implements ITheme {
	private final static  String TAG="ThemeLightButton";
	private Theme mTheme;
	private Context myContext;

	public ThemeLightButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		myContext = context;
		mTheme = new Theme();
		mTheme.set(context, attrs);
		ThemeManager.getInstance().add(this);
	}

	@Override
	public void setTheme(Theme theme) {
		mTheme.set(theme);
		onThemeChanged(ThemeManager.getInstance().getTheme());
	}
	@Override
	public void onThemeChanged(int theme) {
//		int res = mTheme.get(theme);
		//if(res > 0) {
			//setLight(getResources().getColor(res+4), 35);
			

			ColorStateList colorStateList;
			if(!isEnabled()){
				colorStateList = getResources().getColorStateList(R.color.service_item_confirm_color);
			}else{
				switch (theme) {
					case Theme.NORMAL:
						setLight(getResources().getColor(R.color.normal_color), 10);//设置发光的颜色以及半径
						colorStateList = getResources().getColorStateList(R.color.press_selector_text);//点击时候与未点击时候的颜色
						break;
					case Theme.SPORT:
						setLight(getResources().getColor(R.color.sport_color), 10);
						colorStateList = getResources().getColorStateList(R.color.press_selector_text_sport);
						break;
					case Theme.HAD_NORMAL:
						setLight(getResources().getColor(R.color.lightone), 10);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					case Theme.HAD_SPORT:
						setLight(getResources().getColor(R.color.hadsport_color), 8);
//					colorStateList = getResources().getColorStateList(R.color.check_selector_hadsport);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					case Theme.STAR_NORMAL:
						setLight(getResources().getColor(R.color.starnormal_color), 10);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					case Theme.STAR_SPORT:
						setLight(getResources().getColor(R.color.starsport_color), 8);
//					colorStateList = getResources().getColorStateList(R.color.check_selector_hadsport);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					case Theme.BLACKGOLD_NORMAL:
						setLight(getResources().getColor(R.color.blackgoldnormal_color), 10);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					case Theme.BLACKGOLD_SPORT:
						setLight(getResources().getColor(R.color.blackgoldsport_color), 8);
//					colorStateList = getResources().getColorStateList(R.color.check_selector_hadsport);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					case Theme.EYESHOT_NORMAL:
						setLight(getResources().getColor(R.color.eyeshotnormal_color), 10);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					case Theme.EYESHOT_SPORT:
						setLight(getResources().getColor(R.color.eyeshotsport_color), 8);
//					colorStateList = getResources().getColorStateList(R.color.check_selector_hadsport);
						colorStateList = getResources().getColorStateList(R.color.check_selector_hadnormal);
						break;
					default:
						setLight(getResources().getColor(R.color.normal_color), 10);//设置发光的颜色以及半径
						colorStateList = getResources().getColorStateList(R.color.press_selector_text);//点击时候与未点击时候的颜色

						break;
				}
			}
			setTextColor(colorStateList);//设置文本的颜色
	}

}
