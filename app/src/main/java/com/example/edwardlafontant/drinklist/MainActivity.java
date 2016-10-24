package com.example.edwardlafontant.drinklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwardlafontant.drinklist.models.DrinkModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String URL_drinks = "http://blue.cs.montclair.edu/~lafontan/JSONProjects/drinks.json";
    private TextView tvData;
    private ListView lvDrinks;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        lvDrinks = (ListView) findViewById(R.id.lvDrinks);

        new JSONTask().execute(URL_drinks);

    }

    public class JSONTask extends AsyncTask<String, String, List<DrinkModel>> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected List<DrinkModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("drinks");

                List<DrinkModel> drinkModelList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself which is commented below
                     */
                    DrinkModel drinkModel = gson.fromJson(finalObject.toString(), DrinkModel.class);
//                    drinkModel.setMovie(finalObject.getString("movie"));
//                    drinkModel.setYear(finalObject.getInt("year"));
//                    drinkModel.setRating((float) finalObject.getDouble("rating"));
//                    drinkModel.setDirector(finalObject.getString("director"));
//
//                    drinkModel.setDuration(finalObject.getString("duration"));
//                    drinkModel.setTagline(finalObject.getString("tagline"));
//                    drinkModel.setImage(finalObject.getString("image"));
//                    drinkModel.setStory(finalObject.getString("story"));
//
                    drinkModelList.add(drinkModel);
                }
                return drinkModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final List<DrinkModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null) {
                DrinkAdapter adapter = new DrinkAdapter(getApplicationContext(), R.layout.row, result);
                lvDrinks.setAdapter(adapter);
                lvDrinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        DrinkModel drinkModel = result.get(position);
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("drinkModel", new Gson().toJson(drinkModel));
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DrinkAdapter extends ArrayAdapter {

        private List<DrinkModel> drinkModelList;
        private int resource;
        private LayoutInflater inflater;

        public DrinkAdapter(Context contect, int resource, List<DrinkModel> objects) {
            super(contect, resource, objects);
            drinkModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivDrinkIcon = (ImageView) convertView.findViewById(R.id.drinkIcon);
                holder.tvDrink = (TextView) convertView.findViewById(R.id.tvDrink);
                holder.tvTagline = (TextView) convertView.findViewById(R.id.tvTagline);
                holder.tvStory = (TextView) convertView.findViewById(R.id.drinkStory);
                holder.rbDrinkRating = (RatingBar) convertView.findViewById(R.id.rbDrink);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(drinkModelList.get(position).getImage(), holder.ivDrinkIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            holder.tvDrink.setText(drinkModelList.get(position).getDrink());
            holder.tvTagline.setText(drinkModelList.get(position).getTagline());

            // rating bar
            holder.rbDrinkRating.setRating(drinkModelList.get(position).getRating() / 2);

            holder.tvStory.setText(drinkModelList.get(position).getStory());
            return convertView;


        }


        class ViewHolder {
            private ImageView ivDrinkIcon;
            private TextView tvDrink;
            private TextView tvTagline;
            private RatingBar rbDrinkRating;
            private TextView tvStory;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new JSONTask().execute(URL_drinks);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}

