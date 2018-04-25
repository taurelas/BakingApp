package com.leadinsource.bakingapp;

import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import com.leadinsource.bakingapp.db.DataContract;
import com.leadinsource.bakingapp.db.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * For testing the content provider
 */
@RunWith(AndroidJUnit4.class)
public class ContentProviderTest extends ProviderTestCase2<Provider> {

    // required call to super that takes in your Content Provider class and AUTHORITY
    public ContentProviderTest() {
        super(Provider.class, DataContract.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testContext() {
        assertNotNull(getContext());
    }

    @Test
    public void testProvider() {
        assertNotNull(getProvider());
    }

    @Test
    public void queryRecipesTest() {
        Uri uri = DataContract.Recipe.CONTENT_URI;

        Cursor cursor = getProvider().query(uri, null, null, null, null);

        assertEquals(4, cursor.getCount());

    }
}
