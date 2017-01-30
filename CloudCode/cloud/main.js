
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
//Parse.Cloud.define("hello", function(request, response) {
//  response.success("Hello world!");
//});

Parse.Cloud.afterSave("Review", function(request) {
  query = new Parse.Query("StudyLocation");
  query.get(request.object.get("StudyLocation").id, {
    success: function(studyLocation) {
      var numReviews = studyLocation.get("NumReviews");
      var quietness = studyLocation.get("Quietness") * studyLocation.get("NumReviews");
      var wifi = studyLocation.get("WiFi") * studyLocation.get("NumReviews");
      var table = studyLocation.get("Table") * studyLocation.get("NumReviews");
      var outlets = studyLocation.get("Outlets") * studyLocation.get("NumReviews");

      quietness += request.object.get("Quietness");
      wifi += request.object.get("WiFi");
      table += request.object.get("Table");
      outlets += request.object.get("Outlets");
      studyLocation.increment("NumReviews");
      numReviews++;

      quietness /= numReviews;
      wifi /= numReviews;
      outlets /= numReviews;
      table /= numReviews;
      var rating = (quietness + wifi + outlets + table)/4;

      studyLocation.set("Quietness", quietness);
      studyLocation.set("WiFi", wifi);
      studyLocation.set("Outlets", outlets);
      studyLocation.set("Table", table);
      studyLocation.set("Rating", rating);

      studyLocation.save();
    },
    error: function(error) {
      console.error("Got an error " + error.code + " : " + error.message);
    }
  });
});
