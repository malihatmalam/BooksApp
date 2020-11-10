package com.example.booksapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FetchBook extends AsyncTask<String, Void, String> {

    private ArrayList<itemData> values;
    private itemAdapter itemAdapter;
    private RecyclerView recyclerView;
    Context context;

    public FetchBook(Context context, ArrayList<itemData> values,
                     itemAdapter itemAdapter, RecyclerView recyclerView){
        this.context = context;
        this.values = values;
        this.itemAdapter = itemAdapter;
        this.recyclerView = recyclerView;
    }


    @Override
    protected String doInBackground(String... strings) {

        String queryString = strings[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;
        String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
        String QUERY_PARAM = "q";

        Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, queryString).build();

        try {

            URL requesURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requesURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line+"\n");
            }

            if (builder.length() == 0){
                return null;
            }
            bookJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookJSONString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        values = new ArrayList<>();

        try {
            // Karena Object Dulu (yang TIDAK ADA [] artinya tidak memiliki isi Array )
            JSONObject jsonObject = new JSONObject(s);
            // Baru Array (lihat di apinya Google https://www.googleapis.com/books/v1/volumes?)
            JSONArray itemArray = jsonObject.getJSONArray("items");
            // Isi (Array) dari object pertama
            String title = null;
            String author = null;
            String image = null;
            String desc = null;

            //Membuat memasukan data (karena banyak maka butuh pengulangan)
            int i = 0;
            while (i < itemArray.length()){
                // memasukan data buku (untuk mengarahkan ke judul bukunya)
                JSONObject book = itemArray.getJSONObject(i);
                // memasukan volumeinfo (cek api) dari navigasi book dan ke volumeInfo
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                //Membuat try untuk mengantisipasi bila data tidak ada sehingga tidak error
                try {
                    //mengambil judul dari navigasi volume info
                    title = volumeInfo.getString("title");

                    //mencengah bila buku tidak memiliki pengarang
                    if (volumeInfo.has("authors")) {
                        author = volumeInfo.getString("authors");
                    } else {
                        author = "";
                    }

                    //mencengah bila buku tidak memiliki deskripsi
                    if (volumeInfo.has("description")) {
                        desc = volumeInfo.getString("description");
                    } else {
                        desc = "";
                    }

                    //mencengah bila buku tidak memiliki gambar cover
                    if (volumeInfo.has("imageLinks")) {
                        image = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                    } else {
                        image = "";
                    }

                    //membuat item data baru
                    itemData itemData = new itemData();
                    //Memasukan data yang diambil ke dalam item data
                    itemData.itemTitle = title;
                    itemData.itemAuthor = author;
                    itemData.itemDescription = desc;
                    itemData.itemImage = image;

                    values.add(itemData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.itemAdapter = new itemAdapter(context,values);
        this.recyclerView.setAdapter(this.itemAdapter);
    }
}
