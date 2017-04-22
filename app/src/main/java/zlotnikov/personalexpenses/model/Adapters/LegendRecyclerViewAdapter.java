package zlotnikov.personalexpenses.model.Adapters;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import zlotnikov.personalexpenses.R;
import zlotnikov.personalexpenses.model.CircleView;
import zlotnikov.personalexpenses.model.DB.Expense;
import zlotnikov.personalexpenses.model.DB.RealmData;
import zlotnikov.personalexpenses.model.Dialogs.PieChartItemDialog;
import zlotnikov.personalexpenses.model.Listeners.MyRecyclerViewListener;


public class LegendRecyclerViewAdapter extends RecyclerView.Adapter<LegendRecyclerViewAdapter.ViewHolder> {



    private List<Expense> expenseList;
    private List<ItemList> itemList;
    private FragmentManager fragmentManager;

    public LegendRecyclerViewAdapter(List<Expense> expenseList, FragmentManager fragmentManager) {
        this.expenseList = expenseList;
        sortRealmList(this.expenseList);
        Collections.sort(itemList, comparator);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.legend_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.circle.setCircleColor(Color.parseColor(itemList.get(position).getColor()));
        holder.category.setText(itemList.get(position).getCategory());
        holder.money.setText(String.valueOf(itemList.get(position).getMoney()));
        holder.setItemClickListener(new MyRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                PieChartItemDialog.newInstance(itemList.get(position).getCategory(), itemList.get(position).getColor(), itemList.get(position).getMoney())
                        .show(fragmentManager, "pieChartItemDialog");
            }
        });
    }

    public void updateList(){
        expenseList = RealmData.query;
        sortRealmList(expenseList);
        Collections.sort(itemList, comparator);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView money;
        private TextView category;
        private CircleView circle;
        // анонимный класс расширяющий интерфейс-слушателя
        private MyRecyclerViewListener myRecyclerViewListener;

        ViewHolder(View itemView) {
            super(itemView);
            circle = (CircleView)itemView.findViewById(R.id.legendItem_circle);
            money = (TextView)itemView.findViewById(R.id.legendItem_money);
            category = (TextView)itemView.findViewById(R.id.legendItem_category);
            // устанавливаем слушатель
            itemView.setOnClickListener(this);
        }
        // слушатель
        @Override
        public void onClick(View v) {
            myRecyclerViewListener.onItemClick(getLayoutPosition());
        }
        // конструктор для слушателя
        public void setItemClickListener(MyRecyclerViewListener myRecyclerViewListener){
            this.myRecyclerViewListener = myRecyclerViewListener;
        }
    }
    // сортировка данных БД
    private void sortRealmList(List<Expense> realmList){
        itemList = new ArrayList<>();
        // берем первый элемент из БД
        for (int i = 0; i < realmList.size(); i++){
            // проверка
            boolean count = false;
            // категория элемента
            String category = realmList.get(i).getCategory();
            String color = realmList.get(i).getColor();
            int money = realmList.get(i).getMoney();
            // проверяем имеется ли такая категория уже в списке
            for (int j = 0; j < itemList.size(); j++){
                if (itemList.get(j).getCategory().equals(category)){
                    // если имеется то добавляем новую сумму туда сумму
                    itemList.get(j).setMoney(itemList.get(j).getMoney()+ money);
                    // если имеется то меняем счетчик
                    count = true;
                }
            }
            // если не содержит, то записываем
            if (!count) {
                itemList.add(new ItemList(category, color, money));
            }
        }
    }

    private class ItemList{
        private String category;
        private String color;
        private int money;

        ItemList(String category, String color, int money) {
            this.category = category;
            this.color = color;
            this.money = money;
        }

        public String getCategory() {
            return category;
        }

        public String getColor() {
            return color;
        }

        int getMoney() {
            return money;
        }

        void setMoney(int money) {
            this.money = money;
        }
    }
    // компаратор для сортировке по сумме
    public static final Comparator<ItemList> comparator = new Comparator<ItemList>() {
        @Override
        public int compare(ItemList o1, ItemList o2) {
            return o2.getMoney() - o1.getMoney();
        }
    };
}
