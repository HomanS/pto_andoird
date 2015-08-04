package nu.pto.androidapp.base.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.Updateable;
import nu.pto.androidapp.base.adapter.DietListAdapter;
import nu.pto.androidapp.base.db.model.Diet;
import nu.pto.androidapp.base.util.SharedPreferencesHelper;


public class DietListFragment extends BaseFragment implements Updateable {

    private List<Diet> mDiets = new ArrayList<Diet>();

    private DietListAdapter dietListAdapter;

    ListView dietList;

    public DietListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.diet_list_layout, container, false);
        EditText editText = (EditText) rootView.findViewById(R.id.edit_text_search_diet);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString().toLowerCase(Locale.getDefault());
                Log.d(BaseActivity.LOG_TAG, q);
                dietListAdapter.filter(q);
            }
        });

        dietList = (ListView) rootView.findViewById(R.id.dietList);

        dietListAdapter = new DietListAdapter(getActivity());

        dietList.setAdapter(dietListAdapter);

        dietList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Fragment dietFragment = new DietFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("diet_id", mDiets.get(position).dietId);
                dietFragment.setArguments(bundle);
                transaction.replace(R.id.content_layout, dietFragment);
                transaction.addToBackStack("diet");
                transaction.commit();
            }
        });

        getBaseActivity().setTitle(getString(R.string.string_diet));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void updateView() {
        int clientId = SharedPreferencesHelper.getInt(getActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);
        try {
            mDiets = getDatabaseHelper().getDietDao().getClientDiets(clientId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dietListAdapter.setDiets(mDiets);

    }
}
