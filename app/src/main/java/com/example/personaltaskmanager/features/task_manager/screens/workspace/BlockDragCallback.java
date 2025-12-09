package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.graphics.Canvas;
import android.os.Vibrator;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.NotionBlockAdapter;

import java.util.Collections;
import java.util.List;

public class BlockDragCallback extends ItemTouchHelper.Callback {

    private final List<NotionBlock> blocks;
    private final NotionBlockAdapter adapter;
    private final Vibrator vibrator;
    private final MoveHandler handler;

    public BlockDragCallback(List<NotionBlock> blocks,
                             NotionBlockAdapter adapter,
                             Vibrator vibrator,
                             MoveHandler handler) {
        this.blocks = blocks;
        this.adapter = adapter;
        this.vibrator = vibrator;
        this.handler = handler;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder from,
                          @NonNull RecyclerView.ViewHolder to) {

        int p1 = from.getBindingAdapterPosition();
        int p2 = to.getBindingAdapterPosition();

        if (p1 < 0 || p2 < 0) return false;

        Collections.swap(blocks, p1, p2);
        adapter.notifyItemMoved(p1, p2);

        if (handler != null) handler.onItemMove(p1, p2);

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Không hỗ trợ swipe
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {

            if (vibrator != null) vibrator.vibrate(12);

            View item = viewHolder.itemView;

            item.setElevation(20f);
            item.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(120)
                    .start();
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {

        super.clearView(recyclerView, viewHolder);

        View item = viewHolder.itemView;

        item.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(150)
                .start();
        item.setElevation(0f);

        // Báo cho Activity biết user đã thả block
        if (handler != null) handler.onItemDrop();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
