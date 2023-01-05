package vince.stargazer.weathernow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdaptor extends RecyclerView.Adapter<WeatherRVAdaptor.ViewHolder> {
    private final Context context;
    private final ArrayList<WeatherRVModal> weatherRVModalList;

    public WeatherRVAdaptor(Context context, ArrayList<WeatherRVModal> weatherRVModalList) {
        this.context = context;
        this.weatherRVModalList = weatherRVModalList;
    }

    @NonNull
    @Override
    public WeatherRVAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdaptor.ViewHolder holder, int position) {
        WeatherRVModal modal = weatherRVModalList.get(position);
        holder.temperatureTV.setText(modal.getTemperature().concat("°C"));
        Picasso.get().load("http:".concat(modal.getIconURL())).into(holder.conditionTV);
        holder.windTV.setText(modal.getWindSpeed().concat("kph"));
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
            if (t != null)
                holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView windTV, temperatureTV, timeTV;
        private final ImageView conditionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.TVWindSpeed);
            temperatureTV = itemView.findViewById(R.id.TVTemperature);
            timeTV = itemView.findViewById(R.id.TVTime);
            conditionTV = itemView.findViewById(R.id.TVCondition);
        }
    }
}
