package go.pokemon.pokemon.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * EditText to call onFocusChange when when IME_ACTION_DONE fired
 *
 * @author Created by hiking on 2016/8/22.
 */
public class IMEUnfocusEditText extends EditText {

	public IMEUnfocusEditText(Context context) {
		super(context);
		init();
	}

	public IMEUnfocusEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public IMEUnfocusEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setImeOptions(EditorInfo.IME_ACTION_DONE);
		setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (getOnFocusChangeListener() != null) {
						getOnFocusChangeListener().onFocusChange(v, false);
					}
				}
				return false;
			}
		});
	}
}
