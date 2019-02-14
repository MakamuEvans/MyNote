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

import com.elm.mycheck.login.R;
import com.elm.mycheck.login.adapter.TodoAdapter;
import com.elm.mycheck.login.model.Todo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
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

    private NewReceiver newReceiver;
    public List<Todo> todos = new ArrayList<>();
    private RecyclerView recyclerView;
    public TodoAdapter todoAdapter;
    private updateTodo updateTodo;
    private AdView adView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        adView = (AdView) view.findViewById(R.id.ToDoadView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
             //   .addTestDevice("EA28AA409AA1C2A25750D1B354BDD8A9")
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

        IntentFilter intentFilter = new IntentFilter(NewReceiver.SYNC_ACTION);
        newReceiver = new NewReceiver();
        getActivity().registerReceiver(newReceiver, intentFilter);


        IntentFilter filter = new IntentFilter(updateTodo.SYNC_ACTION);
        getActivity().registerReceiver(new updateTodo(), filter);
        recyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler);
        todos = Select.from(Todo.class)
                .orderBy("Id DESC")
                .list();
        Log.e("count", String.valueOf(todos.size()));
        todoAdapter = new TodoAdapter(todos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(todoAdapter);
        return view;
    }

    @Override
    public void onDestroy() {
        if (newReceiver != null)
            getActivity().unregisterReceiver(newReceiver);
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

    public void addNew(Todo todo){
        if (todoAdapter!=null){
            todoAdapter.insert(todo);
            recyclerView.smoothScrollToPosition(0);
        }
    }

    public class NewReceiver extends BroadcastReceiver{
        public static final String SYNC_ACTION = "new_todo_action";
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String newToDo = bundle.getString("todo");
            Gson gson = new Gson();
            Type type = new TypeToken<Todo>(){
            }.getType();
            Todo todo = gson.fromJson(newToDo, type);
            addNew(todo);
        }
    }

    public void updateToDoList(Long id){
        if (todoAdapter!=null){
            todoAdapter.updateItem(id);
        }
    }

    public class updateTodo extends BroadcastReceiver{
        public static final String SYNC_ACTION = "update_todo";
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Long id = bundle.getLong("id");
            updateToDoList(id);
        }
    }
}
