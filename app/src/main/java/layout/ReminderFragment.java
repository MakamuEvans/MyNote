package layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elm.mycheck.login.R;
import com.elm.mycheck.login.adapter.ReminderAdapter;
import com.elm.mycheck.login.model.Reminder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReminderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReminderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    public List<Reminder> reminders = new ArrayList<>();
    public ReminderAdapter reminderAdapter;

    private OnFragmentInteractionListener mListener;

    public ReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReminderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReminderFragment newInstance(String param1, String param2) {
        ReminderFragment fragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ReminderBroadcast reminderBroadcast;
    private AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_reminder, container, false);
        View view=inflater.inflate(R.layout.fragment_reminder,container, false);

        adView = (AdView) view.findViewById(R.id.ReminderadView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
              //  .addTestDevice("EA28AA409AA1C2A25750D1B354BDD8A9")
                .build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.setVisibility(View.GONE);
            }
        });

        IntentFilter intentFilter = new IntentFilter(ReminderBroadcast.NEW_RECEIVER);
        reminderBroadcast = new ReminderBroadcast();
        getActivity().registerReceiver(reminderBroadcast, intentFilter);

        recyclerView = (RecyclerView) view.findViewById(R.id.reminder_recycler);
        reminders = Select.from(Reminder.class)
                .where(Condition.prop("todo").eq(""))
                .orderBy("Id DESC")
                .list();
        reminderAdapter = new ReminderAdapter(reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(reminderAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        if (reminderBroadcast != null)
            getActivity().unregisterReceiver(reminderBroadcast);
        super.onDestroy();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void addNew(Reminder reminder){
        if (reminderAdapter!=null){
            reminderAdapter.newData(reminder);
            recyclerView.smoothScrollToPosition(0);
        }
    }

    public class ReminderBroadcast extends BroadcastReceiver{
        public static final String NEW_RECEIVER = "add_to_ui";
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String newNote = bundle.getString("reminder");
            Gson gson = new Gson();
            Type type = new TypeToken<Reminder>(){
            }.getType();
            Reminder reminder = gson.fromJson(newNote, type);
        }
    }
}
