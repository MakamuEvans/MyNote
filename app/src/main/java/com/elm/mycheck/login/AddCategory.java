package com.elm.mycheck.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.elm.mycheck.login.model.Category;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCategory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCategory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCategory extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddCategory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCategory.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCategory newInstance(String param1, String param2) {
        AddCategory fragment = new AddCategory();
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


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_category, null);
        final Spinner spinner = (Spinner) view.findViewById(R.id.category_spinner);
        final List<String> categories = new ArrayList<>();
        List<Category> data = new ArrayList<>();
        data = Category.listAll(Category.class);

        categories.add("Select Category");
        for (Category category: data){
            categories.add(category.getTitle());
        }
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categories));

        return new AlertDialog.Builder(getActivity())
                .setTitle("Categorize")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText title = (EditText) view.findViewById(R.id.category_title);

                        if (title.getText().toString().isEmpty()){
                            if (spinner.getSelectedItem().toString().equalsIgnoreCase("Select Category")){
                                MDToast.makeText(getActivity(), "Category Unchanged",MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                            }else {
                                //fnd its id in Category table and push back to previous activity
                                String s_title = spinner.getSelectedItem().toString();
                                Category category = Select.from(Category.class)
                                        .where(Condition.prop("title").eq(s_title))
                                        .first();

                                Intent intent = new Intent(AddNote.CategoryReceiver.ACTIION_REP);
                                intent.putExtra("title", category.getId());
                                getActivity().sendBroadcast(intent);
                            }
                        }else {
                            //save new Category and return id
                            Category category = new Category(
                                    title.getText().toString(),
                                    null
                            );
                            category.save();
                            //send broadcast
                            Intent intent = new Intent(AddNote.CategoryReceiver.ACTIION_REP);
                            intent.putExtra("title", category.getId());
                            getActivity().sendBroadcast(intent);
                        }
                        //String category =

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
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
