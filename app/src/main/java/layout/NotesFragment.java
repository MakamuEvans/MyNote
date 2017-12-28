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

import com.example.elm.login.AddNote;
import com.example.elm.login.R;
import com.example.elm.login.adapter.NotesAdapter;
import com.example.elm.login.model.Note;
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
 * {@link NotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public List<Note> notes = new ArrayList<>();
    private RecyclerView recyclerView;
    public NotesAdapter notesAdapter;

    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
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

    private UploadReceiver receiver;
    private SyncReceiver syncReceiver;
    private  DeleteReceiver deleteReceiver;
    private AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes2, container, false);

        adView = (AdView) view.findViewById(R.id.NotesadView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
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

        //register broadcast receiver
        IntentFilter filter = new IntentFilter(AddNote.ACTION_RESP);
        //filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new UploadReceiver();
        getActivity().registerReceiver(receiver, filter);

        IntentFilter intentFilter = new IntentFilter(SyncReceiver.SYNC_ACTION);
        syncReceiver = new SyncReceiver();
        getActivity().registerReceiver(syncReceiver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter(DeleteReceiver.SYNC_ACTION);
        deleteReceiver = new DeleteReceiver();
        getActivity().registerReceiver(deleteReceiver, intentFilter1);

        recyclerView = (RecyclerView) view.findViewById(R.id.notes_recycler);
        notes = Select.from(Note.class)
                .where(Condition.prop("deleteflag").eq(0))
                .orderBy("Id DESC")
                .list();

        notesAdapter = new NotesAdapter(notes, getActivity().getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(notesAdapter);


        return  view;
        //return inflater.inflate(R.layout.fragment_notes2, container, false);
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

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * redraw the recycler -view --all of it
     */

    public void addNew(Note note){
        if (notesAdapter!=null){
            notesAdapter.newData(note, null);
            recyclerView.smoothScrollToPosition(0);

        }
    }

    public void update(Note note){
        if (notesAdapter!=null){
            notesAdapter.updateItem(note);
        }
    }

    public void deleteNote(Note note){
        if (notesAdapter!=null){
            notesAdapter.deleteNote(note);
        }
    }

    /**
     * receive broadcasts
     */
    public class UploadReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("vane", "vane");
            Bundle bundle = intent.getExtras();
            String newNote = bundle.getString("note");
            Gson gson = new Gson();
            Type type = new TypeToken<Note>(){
            }.getType();
            Note note = gson.fromJson(newNote, type);
            addNew(note);

        }
    }
    public class SyncReceiver extends BroadcastReceiver{
        public static final String SYNC_ACTION = "sync_action";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("received_sync", "yes");
            Bundle bundle = intent.getExtras();
            String newNote = bundle.getString("note");
            Gson gson = new Gson();
            Type type = new TypeToken<Note>(){
            }.getType();
            Note note = gson.fromJson(newNote, type);
            update(note);

        }
    }

    public class DeleteReceiver extends  BroadcastReceiver{
        public static final  String SYNC_ACTION = "delete_action";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String newNote = bundle.getString("note");
            Gson gson = new Gson();
            Type type = new TypeToken<Note>(){
            }.getType();
            Note note = gson.fromJson(newNote, type);
            deleteNote(note);
        }
    }
}
