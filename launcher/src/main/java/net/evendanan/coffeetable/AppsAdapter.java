package net.evendanan.coffeetable;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class AppsAdapter extends ListAdapter<AppModel, AppsAdapter.AppsViewModel> {

    private final LayoutInflater mLayoutInflater;
    private final Consumer<AppModel> mClickHandler;

    AppsAdapter(Context context, Consumer<AppModel> clickHandler) {
        super(new Diff());
        mLayoutInflater = LayoutInflater.from(context);
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public AppsViewModel onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AppsViewModel(mLayoutInflater.inflate(R.layout.list_item_icon_text, viewGroup, false), mClickHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull AppsViewModel appsViewModel, int i) {
        final AppModel item = getItem(i);
        appsViewModel.mIcon.setImageDrawable(item.getIcon());
        appsViewModel.mLabel.setText(item.getAppLabel());
        appsViewModel.mCurrentShowing = item;
    }

    static class AppsViewModel extends RecyclerView.ViewHolder {

        private final ImageView mIcon;
        private final TextView mLabel;
        private final Consumer<AppModel> mOnClick;
        private AppModel mCurrentShowing;

        AppsViewModel(@NonNull View itemView, Consumer<AppModel> clickHandler) {
            super(itemView);
            mOnClick = clickHandler;
            mIcon = itemView.findViewById(R.id.icon);
            mLabel = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(v -> mOnClick.accept(mCurrentShowing));
        }
    }

    private static class Diff extends DiffUtil.ItemCallback<AppModel> {

        @Override
        public boolean areItemsTheSame(@NonNull AppModel appModel, @NonNull AppModel t1) {
            return appModel.getPackageName().equals(t1.getPackageName()) && appModel.getActivityName().equals(t1.getActivityName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AppModel appModel, @NonNull AppModel t1) {
            return areItemsTheSame(appModel, t1);
        }
    }
}
