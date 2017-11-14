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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elm.login.Navigation;
import com.example.elm.login.R;
import com.example.elm.login.ReminderName;
import com.example.elm.login.adapter.MilestoneAdapter;
import com.example.elm.login.model.Milestones;
import com.example.elm.login.utils.Utils;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToDo2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ToDo2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDo2 extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ToDo2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToDo2.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDo2 newInstance(String param1, String param2) {
        ToDo2 fragment = new ToDo2();
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

    String todoId;
    RecyclerView recyclerView;
    public List<Milestones> milestones = new ArrayList<>();
    public MilestoneAdapter milestoneAdapter;
    private  TaskReceiver taskReceiver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do2, container, false);
        LinearLayout add_task = (LinearLayout) view.findViewById(R.id.add_task);
        todoId = getArguments().getString("id");

        IntentFilter intentFilter = new IntentFilter(TaskReceiver.ACTIION_REP);
        taskReceiver= new TaskReceiver();
        getActivity().registerReceiver(taskReceiver, intentFilter);

        recyclerView = (RecyclerView) view.findViewById(R.id.task_recycler);
        milestones = Milestones.listAll(Milestones.class);
        milestones = Select.from(Milestones.class)
                .where(Condition.prop("todoid").eq(todoId))
                .list();

        milestoneAdapter = new MilestoneAdapter(milestones);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(milestoneAdapter);

        TextView close = (TextView) view.findViewById(R.id.finish);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int page = 3;
                Intent intent = new Intent(getActivity(), Navigation.class);
                intent.putExtra("page", page);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMilestone addMilestone = new AddMilestone();
                addMilestone.show(getActivity().getFragmentManager(), "Dialog");
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
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

    public void onCompleted(String title) {
        Milestones milestones = new Milestones(todoId,title,null,false);
        milestones.save();
        if (milestoneAdapter!=null){
            milestoneAdapter.insert(milestones);
        }

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

    public class TaskReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "add_new_task";
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String title = bundle.getString("title");
            onCompleted(title);
        }
    }
}
