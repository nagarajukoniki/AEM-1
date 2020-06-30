$(function() {
  $('.left-nav-chevron__button').on('click', function(event) {
    var targetId = event.target.id.replace('-icon', '');
    if (targetId) {
      var element = $('#' + targetId + '-nested');
      var iconElement = $('#' + targetId + '-icon');
      var surfaceElement = $('#' + targetId + '-surface');
      var leftNavExpanded = this.leftNavExpanded;
      if (!leftNavExpanded) leftNavExpanded = new Set([]);
      // Close nested menu
      if (leftNavExpanded.has(targetId)) {
        iconElement.removeClass('left-nav-chevron-open__img')
        iconElement.addClass('left-nav-chevron-closed__img');
        surfaceElement.removeClass('left-nav-surface-level-expanded__wrapper');
        element.removeClass('left-nav-group-open__wrapper').addClass('left-nav-group-closed__wrapper');
        $('#' + targetId).attr('aria-expanded', 'false');
        $('#' + targetId + '-link').attr('aria-expanded', 'false');
        leftNavExpanded.delete(targetId);
      }
      // Open nested menu
      else {
        iconElement.removeClass('left-nav-chevron-closed__img')
        iconElement.addClass('left-nav-chevron-open__img');
        surfaceElement.addClass('left-nav-surface-level-expanded__wrapper');
        element.removeClass('left-nav-group-closed__wrapper').addClass('left-nav-group-open__wrapper');
        $('#' + targetId).attr('aria-expanded', 'true');
        $('#' + targetId + '-link').attr('aria-expanded', 'true');
        leftNavExpanded.add(targetId);
      }
      this.leftNavExpanded = leftNavExpanded;
    }
  });
});
// $(function() {
//   var test = $('#3-nested li');
//   $('#3-nested li').each(function(i) {
//     $(this).delay(100 * i).fadeIn(500);
//   });
// });
