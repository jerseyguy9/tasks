package com.todoroo.astrid.activity;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.timsu.astrid.R;
import com.todoroo.andlib.utility.AndroidUtilities;
import com.todoroo.astrid.adapter.FilterAdapter;
import com.todoroo.astrid.api.Filter;
import com.todoroo.astrid.api.FilterListItem;
import com.todoroo.astrid.service.ThemeService;
import com.todoroo.astrid.ui.ListDropdownPopover;

public class TaskListWrapperActivity extends AstridWrapperActivity {

    public static final String TOKEN_SELECTED_FILTER = "selectedFilter";
    private View listsNav;
    private TextView lists;

    private FilterAdapter listDropdownAdapter;
    private ListDropdownPopover popover;

    private final OnItemClickListener listClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Filter item = listDropdownAdapter.getItem(position);
            onFilterItemClicked(item);
            popover.dismiss();
            lists.setText(item.title);
        }
    };

    private final OnClickListener popupMenuClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setListsDropdownSelected(true);
            popover.show(v);
        }
    };
    /**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    ThemeService.applyTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list_wrapper_activity);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.header_nav_views);

        listsNav = actionBar.getCustomView().findViewById(R.id.lists_nav);
		lists = (TextView) actionBar.getCustomView().findViewById(R.id.list_title);

		View container = findViewById(R.id.filterlist_fragment_container);
		if (container != null) {
		    mMultipleFragments = true;
		    actionBar.setDisplayHomeAsUpEnabled(false);
		    actionBar.getCustomView().findViewById(R.id.list_disclosure_arrow).setVisibility(View.GONE);
		    listsNav.setOnClickListener(null);
		} else {
		    mMultipleFragments = false;
		    actionBar.setDisplayHomeAsUpEnabled(true);
		    listsNav.setOnClickListener(popupMenuClickListener);
		    createPopover();
		}

		Filter savedFilter = getIntent().getParcelableExtra(TaskListActivity.TOKEN_FILTER);
		setupTasklistFragmentWithFilter(savedFilter);
		if (savedFilter != null)
		    lists.setText(savedFilter.title);
		setupFilterlistFragment();
	}

	private void createPopover() {
	    popover = new ListDropdownPopover(TaskListWrapperActivity.this);
        popover.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setListsDropdownSelected(false);
            }
        });
	}

	public void setupPopoverWithFilterList(FilterListActivity fla) {
	    if (popover != null) {
	        View view = fla.getView();
	        if (view != null) {
	            FrameLayout parent = (FrameLayout) view.getParent();
	            if (parent != null)
	                parent.removeView(view);
	            popover.setContent(view);
	        }
	    }
	}

	@Override
	public boolean onFilterItemClicked(FilterListItem item) {
	    if (popover != null)
	        popover.dismiss();
	    return super.onFilterItemClicked(item);
	}

	private void setListsDropdownSelected(boolean selected) {
	    int oldTextColor = lists.getTextColors().getDefaultColor();
	    int textStyle = (selected ? R.style.TextAppearance_ActionBar_ListsHeader_Selected : R.style.TextAppearance_ActionBar_ListsHeader);
	    lists.setTextAppearance(this, textStyle);
	    listsNav.setBackgroundColor(selected ? oldTextColor : android.R.color.transparent);
	}

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (popover != null)
            popover.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
    }

    public void setSelectedItem(Filter item) {
       lists.setText(item.title);
    }

    @Override
    public void finish() {
        super.finish();
        AndroidUtilities.callOverridePendingTransition(this, R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
