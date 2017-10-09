package de.greenrobot.performance.room;

import android.arch.persistence.room.Room;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.performance.BasePerfTestCase;
import de.greenrobot.performance.Benchmark;

/**
 * https://developer.android.com/topic/libraries/architecture/room.html
 */
public class PerfTestRoom extends BasePerfTestCase {

    private static final String DB_NAME = "room-test.db";

    private AppDatabase db;
    private IndexedStringEntityDao indexedStringEntityDao;
    private SimpleEntityNotNullDao simpleEntityNotNullDao;

    @Override
    public void setUp() throws Exception {
        db = Room.databaseBuilder(getTargetContext(), AppDatabase.class, DB_NAME).build();
        indexedStringEntityDao = db.indexedStringEntityDao();
        simpleEntityNotNullDao = db.simpleEntityNotNullDao();
    }

    @Override
    public void tearDown() throws Exception {
        db.close();
        getTargetContext().deleteDatabase(DB_NAME);
    }

    @Override
    protected void doOneByOneCrudRun(int count) throws Exception {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            list.add(createSimpleEntityNotNull(i));
        }

        startClock();
        for (int i = 0; i < count; i++) {
            simpleEntityNotNullDao.insert(list.get(i));
        }
        stopClock(Benchmark.Type.ONE_BY_ONE_CREATE);

        startClock();
        for (int i = 0; i < count; i++) {
            simpleEntityNotNullDao.update(list.get(i));
        }
        stopClock(Benchmark.Type.ONE_BY_ONE_UPDATE);

        deleteAll();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void doBatchCrudRun(int count) throws Exception {
        final List<SimpleEntityNotNull> list = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            list.add(createSimpleEntityNotNull(i));
        }

        startClock();
        simpleEntityNotNullDao.insert(list);
        stopClock(Benchmark.Type.BATCH_CREATE);

        startClock();
        simpleEntityNotNullDao.update(list);
        stopClock(Benchmark.Type.BATCH_UPDATE);

        startClock();
        List<SimpleEntityNotNull> reloaded = simpleEntityNotNullDao.getAll();
        stopClock(Benchmark.Type.BATCH_READ);

        startClock();
        for (int i = 0; i < reloaded.size(); i++) {
            SimpleEntityNotNull entity = reloaded.get(i);
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleShort();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
            entity.getSimpleByteArray();
        }
        stopClock(Benchmark.Type.BATCH_ACCESS);

        startClock();
        deleteAll();
        stopClock(Benchmark.Type.BATCH_DELETE);
    }

    private void deleteAll() {
        simpleEntityNotNullDao.deleteAll();
    }

    private static SimpleEntityNotNull createSimpleEntityNotNull(Long key) {
        if (key == null) {
            return null;
        }
        SimpleEntityNotNull entity = new SimpleEntityNotNull();
        entity.setId(key);
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(Byte.MAX_VALUE);
        entity.setSimpleShort(Short.MAX_VALUE);
        entity.setSimpleInt(Integer.MAX_VALUE);
        entity.setSimpleLong(Long.MAX_VALUE);
        entity.setSimpleFloat(Float.MAX_VALUE);
        entity.setSimpleDouble(Double.MAX_VALUE);
        entity.setSimpleString("greenrobot greenDAO");
        byte[] bytes = {42, -17, 23, 0, 127, -128};
        entity.setSimpleByteArray(bytes);
        return entity;
    }
}
