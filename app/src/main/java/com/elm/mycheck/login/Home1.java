package com.elm.mycheck.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elm.mycheck.login.model.Note;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.model.Todo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home1 extends Fragment {
    private TextView total_notes,week_notes,fav_notes,total_alarms,week_alarms, active_alarms,type_alarms,missed_alarms,
            total_todo,week_todo,completed_todo,incomplete_todo;
    private ImageView expandToDo,expandNotes,expandReminders;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Home1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home1.
     */
    // TODO: Rename and change types and number of parameters
    public static Home1 newInstance(String param1, String param2) {
        Home1 fragment = new Home1();
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
    private AdView mAdView,mAdView1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home1, container, false);

        mAdView = (AdView) view.findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        mAdView1 = (AdView) view.findViewById(R.id.adView2);
        mAdView1.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("EA28AA409AA1C2A25750D1B354BDD8A9")
                .build();
        mAdView.loadAd(adRequest);
        mAdView1.loadAd(adRequest);

        mAdView1.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                mAdView1.setVisibility(View.GONE);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdView1.setVisibility(View.GONE);
            }
        });
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                mAdView.setVisibility(View.GONE);
            }
        });

        total_notes = (TextView) view.findViewById(R.id.total_notes);
        week_notes = (TextView) view.findViewById(R.id.week_notes);
        fav_notes = (TextView) view.findViewById(R.id.favourite_notes);

        total_alarms = (TextView) view.findViewById(R.id.total_alarms);
        week_alarms = (TextView) view.findViewById(R.id.week_alarms);
        active_alarms = (TextView) view.findViewById(R.id.active_alarms);
        type_alarms = (TextView) view.findViewById(R.id.type_alarms);

        total_todo = (TextView) view.findViewById(R.id.total_todo);
        week_todo = (TextView) view.findViewById(R.id.week_todo);
        completed_todo = (TextView) view.findViewById(R.id.completed_todo);
        incomplete_todo = (TextView) view.findViewById(R.id.incomplete_todo);

        expandToDo = (ImageView) view.findViewById(R.id.expand_todo);
        expandNotes = (ImageView) view.findViewById(R.id.expand_notes);
        expandReminders = (ImageView) view.findViewById(R.id.expand_reminders);
        expandToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Homev2.class);
                intent.putExtra("page", 3);
                startActivity(intent);
            }
        });
        expandNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Homev2.class);
                intent.putExtra("page", 1);
                startActivity(intent);
            }
        });
        expandReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Homev2.class);
                intent.putExtra("page", 2);
                startActivity(intent);
            }
        });

        init();

        return view;
    }

    private void init(){

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        //notes
        total_notes.setText(String.valueOf(Select.from(Note.class).where(Condition.prop("deleteflag").eq("0")).count()));
        fav_notes.setText(String.valueOf(Select.from(Note.class).where(Condition.prop("deleteflag").eq("0")).where(Condition.prop("favourite").eq("1")).count()));
        week_notes.setText(String.valueOf(Select.from(Note.class).where(Condition.prop("deleteflag").eq("0")).where(Condition.prop("createdAt").gt(formatter.format(cal.getTime().getTime()))).count()));

        //reminders
        total_alarms.setText(String.valueOf(Select.from(Reminder.class).count()));
        active_alarms.setText(String.valueOf(Select.from(Reminder.class).where(Condition.prop("status").eq("1")).count()));
        week_alarms.setText(String.valueOf(Select.from(Reminder.class).where(Condition.prop("createdAt").gt(formatter.format(cal.getTime().getTime()))).count()));
        String repeating = String.valueOf(Select.from(Reminder.class).where(Condition.prop("repeat").isNotNull()).count());
        String oneTime = String.valueOf(Select.from(Reminder.class).where(Condition.prop("repeat").isNull()).count());

        //todo
        total_todo.setText(String.valueOf(Select.from(Todo.class).count()));
        week_todo.setText(String.valueOf(Select.from(Todo.class).where(Condition.prop("createdAt").gt(formatter.format(cal.getTime().getTime()))).count()));


        type_alarms.setText("("+repeating+" , "+oneTime+")");


    }

    @Override
    public void onResume() {
        super.onResume();
        init();
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
}
