package layout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.elm.mycheck.login.R;
import com.elm.mycheck.login.model.Todo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.orm.query.Select;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToDo1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ToDo1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDo1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ToDo1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToDo1.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDo1 newInstance(String param1, String param2) {
        ToDo1 fragment = new ToDo1();
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

    private AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do1, container, false);

        adView = (AdView) view.findViewById(R.id.Todo1adView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
             //   .addTestDevice("EA28AA409AA1C2A25750D1B354BDD8A9")
                .build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }
        });

        TextView next= (TextView) view.findViewById(R.id.todo_next);
        TextView cancel= (TextView) view.findViewById(R.id.todo_cancel);
        final EditText title = (EditText) view.findViewById(R.id.todo_title);
        final EditText description = (EditText) view.findViewById(R.id.todo_description);

        //current date
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final Date date = new Date();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()){
                    //Toast.makeText(getContext(), "The Title should be filled", Toast.LENGTH_SHORT).show();
                    MDToast.makeText(getContext(),"Title can not be blank!",MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                    return;
                }
                Todo todo = new Todo(
                        title.getText().toString(),
                        description.getText().toString(),
                        null,
                        "0",
                        dateFormat.format(date),
                        dateFormat.format(date)
                        );
                todo.save();

                //update todo recycler
                if (Select.from(Todo.class).count() > 1){
                    String dt = new Gson().toJson(todo);
                    Intent intent = new Intent();
                    intent.setAction(EventsFragment.NewReceiver.SYNC_ACTION);
                    intent.putExtra("todo", dt);
                    getActivity().sendBroadcast(intent);
                }


                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(todo.getId()));
                bundle.putString("title", todo.getTitle());
                ToDo2 toDo2 = new ToDo2();
                toDo2.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container_frame, toDo2).commit();
            }
        });
        return  view;

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
    }
*/
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
