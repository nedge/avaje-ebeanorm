package com.avaje.tests.model.selfref;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;

public class TestSelfRefExample extends BaseTestCase {

  @Test
  public void test() {
    
    SelfRefExample e1 = new SelfRefExample("test1", null);
    SelfRefExample e2 = new SelfRefExample("test1", e1);
    SelfRefExample e3 = new SelfRefExample("test2", e2);
    SelfRefExample e4 = new SelfRefExample("test2", e1);
    SelfRefExample e5 = new SelfRefExample("test2", e4);
    SelfRefExample e6 = new SelfRefExample("test2", e2);
    SelfRefExample e7 = new SelfRefExample("test3", e3);
    SelfRefExample e8 = new SelfRefExample("test3", e7);
 
    Ebean.save(e1);
    Ebean.save(e2);
    Ebean.save(e3);
    Ebean.save(e4);
    Ebean.save(e5);
    Ebean.save(e6);
    Ebean.save(e7);
    Ebean.save(e8);
 
    Query<SelfRefExample> examples = Ebean.createQuery(SelfRefExample.class);
    List<SelfRefExample> list = examples.where().eq("name", "test1").findList();
 
    Assert.assertEquals(2, list.size());
    
    //The first Example is e1. e2 is the first child of e1. e3 is the first child of e2.
    SelfRefExample e3Example = list.get(0).getChildren().get(0).getChildren().get(0);
    //ID should be 3, since this has to be e3.
    Assert.assertEquals(e3.getId(), e3Example.getId());
    
 
    //Now that we have e3, it should also have a child: e7.
    Assert.assertEquals(1, e3Example.getChildren().size());
    Assert.assertEquals(e7.getId(), e3Example.getChildren().get(0).getId());
     
    // If we get all the items, you can see the structure goes down a fair bit further.
    Query<SelfRefExample> examples2 = Ebean.createQuery(SelfRefExample.class).orderBy("id asc");
    List<SelfRefExample> list2 = examples2.findList();
    
    Assert.assertEquals(e1.getId(), list2.get(0).getId());
    Assert.assertEquals(e2.getId(), list2.get(0).getChildren().get(0).getId());
    Assert.assertEquals(e3.getId(), list2.get(0).getChildren().get(0).getChildren().get(0).getId());
    Assert.assertEquals(e7.getId(), list2.get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getId());
  }
}
