package net.evendanan.coffeetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.evendanan.coffeetable.model.AppModel;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AppsAdapterBase<VH extends AppsAdapterBase.AppsViewModel> extends ListAdapter<AppModel, VH> {

    private final LayoutInflater mLayoutInflater;
    private final Consumer<AppModel> mClickHandler;

    public AppsAdapterBase(Context context, Consumer<AppModel> clickHandler) {
        super(new Diff());
        mLayoutInflater = LayoutInflater.from(context);
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return createAppsViewHolder(mLayoutInflater, viewGroup, mClickHandler);
    }

    protected abstract VH createAppsViewHolder(LayoutInflater inflater, ViewGroup parent, Consumer<AppModel> clickHandler);

    @Override
    public void onBindViewHolder(@NonNull VH appsViewModel, int i) {
        final AppModel item = getItem(i);
        appsViewModel.bind(item);
    }

    public static class AppsViewModel extends RecyclerView.ViewHolder {

        private final ImageView mIcon;
        private final TextView mLabel;
        private final Consumer<AppModel> mOnClick;
        private AppModel mCurrentShowing;

        public AppsViewModel(@NonNull View itemView, Consumer<AppModel> clickHandler) {
            super(itemView);
            mOnClick = clickHandler;
            mIcon = itemView.findViewById(R.id.icon);
            mLabel = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(v -> mOnClick.accept(mCurrentShowing));
        }

        public void bind(AppModel item) {
            mIcon.setImageDrawable(item.getIcon());
            mLabel.setText(item.getAppLabel());
            mCurrentShowing = item;
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
