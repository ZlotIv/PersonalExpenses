package zlotnikov.personalexpenses.model.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.List;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.CircleView;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;

public class ExpensesListRecyclerAdapter extends RecyclerView.Adapter<ExpensesListRecyclerAdapter.ViewHolder>{

    private List<Expense> list;

    public ExpensesListRecyclerAdapter(List<Expense> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.expenses_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense item = list.get(position);
        holder.money.setText(String.valueOf(item.getMoney()));
        holder.date.setText(String.valueOf(new SimpleDateFormat("dd.MM.yy HH:mm").format(item.getDate())));
        holder.category.setText(item.getCategory());
        holder.circle.setCircleColor(Color.parseColor(item.getColor()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    // обновление списка
    public void update(){
        this.list = RealmData.query;
        this.notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView money;
        private TextView date;
        private TextView category;
        private CircleView circle;

        ViewHolder(View itemView) {
            super(itemView);
            circle = (CircleView)itemView.findViewById(R.id.circleViewItem);
            money = (TextView)itemView.findViewById(R.id.moneyItem);
            date = (TextView)itemView.findViewById(R.id.dateItem);
            category = (TextView)itemView.findViewById(R.id.categoryItem);
        }
    }
}
