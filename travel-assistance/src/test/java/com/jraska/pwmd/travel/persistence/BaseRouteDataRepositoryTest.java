package com.jraska.pwmd.travel.persistence;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.jraska.BaseTest;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.persistence.TravelAssistanceDbHelper;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public abstract class BaseRouteDataRepositoryTest extends BaseTest {
  //region Fields

  private SQLiteOpenHelper _openHelper;
  protected TravelDataRepository _repository;

  //endregion

  //region Setup Methods

  @Before
  public void setUp() throws Exception {
    _openHelper = new TravelAssistanceDbHelper(getApplication(), "testDataDb.sqlite");
    _repository = createRepository(_openHelper);
  }

  @After
  public void tearDown() throws Exception {
    _openHelper.close();
  }

  //endregion

  //region Test Methods

  @Test
  public void testInsert() throws Exception {
    _repository.insertRoute(createRouteData());

    assertThat(_repository.selectAllRouteDescriptions(), hasSize(1));
  }

  @Test
  public void testUpdate() {

    RouteData routeData = createRouteData();
    _repository.insertRoute(routeData);

    ArrayList<Position> positions = new ArrayList<>(routeData.getPath().getPoints());
    positions.add(generatePosition());
    RouteData routeData2 = new RouteData(routeData.getDescription(), new Path(positions));

    _repository.updateRoute(routeData2);

    //try get all
    RouteData updatedData = _repository.selectRouteData(routeData2.getId());
    assertThat(updatedData.getPath(), equalTo(routeData2.getPath()));
  }

  @Test
  public void testDelete() throws Exception {
    RouteData routeData = createRouteData();

    _repository.insertRoute(routeData);

    _repository.deleteRoute(routeData.getId());

    assertThat(_repository.selectAllRouteDescriptions(), hasSize(0));
  }

  //endregion

  //region Methods

  protected abstract TravelDataRepository createRepository(SQLiteOpenHelper openHelper);

  @NonNull
  protected RouteData createRouteData() {
    List<Position> positions = generatePositions(3);

    //build test route
    RouteDescription routeDescription = new RouteDescription(UUID.randomUUID(), new Date(), new Date(), "Test");
    return new RouteData(routeDescription, new Path(positions));
  }

  @NonNull
  protected List<Position> generatePositions(int pointsCount) {
    List<Position> positions = new ArrayList<>(pointsCount);
    for (int i = 0; i < pointsCount; i++) {
      positions.add(generatePosition());
    }
    return positions;
  }

  protected Position generatePosition() {
    Random random = new Random();
    return new Position(random.nextDouble() * 50, random.nextDouble() * 50, System.currentTimeMillis(), 30.0f, "GPS");
  }

  //endregion
}