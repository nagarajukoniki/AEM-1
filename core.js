// START animated labels
var HEADERSMALL_DESKTOP = 1198;
function setUserIdFocus() {
  $('.main-header-login-username').focus();
}

function animateFormLabels() {
  var $inputs = $('input.js-floating-label-form');
  var inputSearchMobileContainer = $('.main-header-mobile-search');
  $inputs.each(function() {
    var input = $(this);
    var labels = $('label[for="' + input.attr('id') + '"]');
    input
      .focus(function() {
        labels.addClass('js-label-float-up');
      })
      .blur(function() {
        var inputValue = input.val().length;
        if (inputValue > 0) {
          if (
            window.matchMedia('(max-width: ' + HEADERSMALL_DESKTOP + 'px)')
              .matches
          ) {
            inputSearchMobileContainer.addClass('input-has-value');
          }
          labels.addClass('js-label-float-up');
        } else {
          if (
            window.matchMedia('(max-width: ' + HEADERSMALL_DESKTOP + 'px)')
              .matches
          ) {
            inputSearchMobileContainer.removeClass('input-has-value');
          }
          labels.removeClass('js-label-float-up');
        }
      });
  });
  setUserIdFocus();
}
$(window).on('load', animateFormLabels);
// END animated labels

/*Start highlight mobile nav selected:*/
(function _highLightMobileNav_() {
  //runs immediately
  $(function() {
    domReady();
  });

  function domReady() {
    setTimeout(fnTimeout, 0);

    function fnTimeout() {
      if ($('.main-header-mobile-nav-toggle').length > 0) {
        var mobileMenuButtonClicked = false;
        $('.main-header-mobile-nav-toggle').click(function() {
          var chosenLang = $(':hidden#languagePref').val();
          if (mobileMenuButtonClicked == true) {
            return;
          }
          mobileMenuButtonClicked = true;
          var mainHeaderNavItems = document.getElementsByClassName(
            'main-header-nav-item'
          );
          var mainHeaderNavItemsSecondary = document.getElementsByClassName(
            'main-header-nav-item-secondary'
          );
          var mainHeaderNavItemsTertiary = document.getElementsByClassName(
            'main-header-nav-item-tertiary'
          );
          var navArray = [
            'skipped!',
            mainHeaderNavItems,
            mainHeaderNavItemsSecondary,
            mainHeaderNavItemsTertiary,
          ];
          //"skipped!" is added to navArray so we dont pickup the "Home" breadcrumb
          //if first breadcrumb is not "Home" remove "skipped!" from navArray
          if (
            $('.main-header-nav-item a')
              .eq(0)
              .get(0)
              .innerHTML.trim() == 'home'
          ) {
            navArray.shift();
          }
          var breadCrumbItems = document.getElementsByClassName(
            'breadcrumb-item'
          );
          var breadCrumbItemsLength = breadCrumbItems.length;
          var currentBreadCrumbIndex = 0;
          for (
            var currentBreadCrumbIndex = 0;
            currentBreadCrumbIndex < breadCrumbItemsLength;
            currentBreadCrumbIndex++
          ) {
            forEachBreadCrumbItem(currentBreadCrumbIndex);
          }

          function forEachBreadCrumbItem(breadCrumbIndex) {
            var breadcrumbHTML = (function() {
              var breadCrumbItem = breadCrumbItems[breadCrumbIndex];
              var breadcrumbItemA = breadCrumbItem.getElementsByTagName('A')[0];
              return breadcrumbItemA
                ? breadcrumbItemA.innerHTML.trim()
                : breadCrumbItem.innerHTML.trim();
            })();
            if (
              breadcrumbHTML == 'Home' ||
              breadcrumbHTML == 'é¦–é¡µ' ||
              breadcrumbHTML == 'é¦–é '
            ) {
              return;
            }
            var currentNavArray = navArray[breadCrumbIndex];
            currentNavArrayForEach(currentNavArray, breadcrumbHTML);

            function currentNavArrayForEach(currentNavArray, breadcrumbHTML) {
              for (var i = 0, j = currentNavArray.length; i < j; i++) {
                var currentNavItemA = currentNavArray[i].getElementsByTagName(
                  'A'
                )[0];
                var currentNavLinkHTML = currentNavItemA.innerHTML.trim();
                if (breadcrumbHTML == currentNavLinkHTML) {
                  if (currentNavArray[i].className.indexOf('expandable') > -1) {
                    currentNavItemA.click();
                    if (
                      breadCrumbIndex == breadCrumbItemsLength - 1 &&
                      navArray[breadCrumbIndex + 1]
                    ) {
                      if (chosenLang == 'en_US') {
                        var newHTML = breadcrumbHTML + ' Overview';
                      } else if (chosenLang == 'zh_CN') {
                        var newHTML = breadcrumbHTML + ' æ¦‚è§ˆ';
                      } else if (chosenLang == 'zh_TW') {
                        var newHTML = breadcrumbHTML + ' æ¦‚è¦½';
                      }
                      currentNavArrayForEach(
                        navArray[breadCrumbIndex + 1],
                        newHTML
                      );
                    }
                  } else {
                    currentNavItemA.style.color = '#50b948';
                  }
                }
              }
            }
          }
          currentBreadCrumbIndex;
          if (location.pathname == '/branch-locator.page') {
            $('a[href="/branch-locator.page"]').get(0).style.color = '#50b948';
          }
        });
      }
    }
  }
})();
/*End*/

var BREAKPOINT_MOBILE = 767;

$(function() {
  var searchMobile = $('#header-search-mobile');

  // Header search on focus and blur
  searchMobile
    .focus(function() {
      $(this)
        .closest('.main-header-nav-container')
        .addClass('input-active');
    })
    .blur(function() {
      $(this)
        .closest('.main-header-nav-container')
        .removeClass('input-active');
    });

  // HK MOBILE NAV
  $('.main-header-mobile-nav-toggle').on('click', function() {
    $('html').toggleClass('nav-open');

    if ($('html').hasClass('nav-open')) {
      $(window).scrollTop(0);
      $('.hk-language__options').hide();
      $('.hk-language__button').attr('aria-expanded', 'false');
    }
  });

  // Tree navigation - secondary nav toggles
  $('.main-header-nav-item.expandable > a').on('click', function(event) {
    if ($(this).parents('.nav-open').length) {
      event.preventDefault();
      event.stopPropagation();
      $(this)
        .parent()
        .siblings()
        .removeClass('expanded');
      $(this)
        .parent()
        .toggleClass('expanded');
    }
  });

  // Tree navigation - tertiary nav toggles
  $('.main-header-nav-item-secondary.expandable > a').on('click', function(
    event
  ) {
    if ($(this).parents('.nav-open').length) {
      event.preventDefault();
      event.stopPropagation();
      $(this)
        .parent()
        .toggleClass('expanded');
    }
  });

  // Jump to expanded menu item
  $('.main-header-nav-item a').on('click', function(event) {
    var $container = $(event.target).parent();
    var containerAbsoluteTop = $container.offset().top;
    var containerRelativeTop = $container.position().top;
    var containerHeight = $container.outerHeight();
    var $scrollableParent, listScrollTop;

    // Do nothing if the bottom of the container is already above the bottom of the viewport, or if the item is not expanded
    if (
      !$container.hasClass('expanded') ||
      containerAbsoluteTop + containerHeight - listScrollTop <
        window.innerHeight
    ) {
      return event;
    }

    // Tablet behavior
    if (
      window.innerWidth > BREAKPOINT_MOBILE &&
      $container.hasClass('main-header-nav-item-secondary')
    ) {
      $scrollableParent = $container.parents('ul');
      listScrollTop = $scrollableParent.scrollTop();
      // Just show the bottom of the opened menu
      $scrollableParent.animate(
        {
          scrollTop: containerRelativeTop + listScrollTop,
        },
        100
      );
    }
  });

  $('.main-header-login-username').focus();

  // Expanding mobile search bar
  $('.main-header-search-input').on('focusin', function() {
    $('.main-header-mobile-search').addClass('expanded');
  });
  $('.main-header-search-input').on('focusout', function() {
    $('.main-header-mobile-search').removeClass('expanded');
  });

  // TAB COMPONENT
  var tabList = $('.tab-navigation__list');
  var tabListActive = $('.tab-navigation__list.active');
  var tabContents = $('.tab-contents');
  var tableHeader = $('.tab-header');
  var tabActive = $('#' + tabListActive.attr('aria-controls'));

  tabListActive.each(function() {
    $('#' + $(this).attr('aria-controls'))
      .find('.tab-contents')
      .show()
      .attr('aria-hidden', 'false');
  });

  tabList.on('click', function() {
    var tabActive = $('#' + $(this).attr('aria-controls'));

    $(this)
      .closest('.default-component__wrapper')
      .find('.full-bleed__wrapper--tabletMobile-only')
      .find('.tab-contents')
      .hide()
      .attr('aria-hidden', 'true');

    $(this)
      .closest('.tab-navigation__container')
      .find('.tab-navigation__list')
      .removeClass('active')
      .attr('aria-expanded', 'false');

    if ($(this).hasClass('active')) {
      return false;
    }

    $(this).addClass('active');
    $(this).attr('aria-expanded', 'true');
    tabActive
      .find('.tab-contents')
      .show()
      .attr('aria-hidden', 'false');
  });

  if ($('.tab-available')) {
    tableHeader.on('click', function() {
      $(this).closest('.full-bleed__wrapper--tabletMobile-only')
        .find('.tab-header')
        .removeClass('accordion__item--open');

      tabActive = $(this).attr('aria-controls');

      $(this)
        .closest('.default-component__wrapper')
        .find('.tab-navigation__container')
        .find('.tab-navigation__list')
        .removeClass('active');

      $(this).closest('.full-bleed__wrapper--tabletMobile-only')
        .find('.tab-contents')
        .slideUp()
        .attr('aria-hidden', 'true');

      $(this).attr('aria-expanded', 'false');

      if (
        $(this)
          .next()
          .css('display') === 'none'
      ) {
        $('li[aria-controls="' + tabActive + '"]').addClass('active');
        $(this)
          .closest('.tab-item')
          .find('.tab-header--fixed')
          .addClass('acitve');
        $(this)
          .addClass('accordion__item--open')
          .attr('aria-expanded', 'true');
        $(this)
          .next()
          .slideDown();
        $(this)
          .next()
          .attr('aria-hidden', 'false');
      }
      return false;
    });
  }

  $(window).on('load resize', function() {
    var tabListActive = $('.tab-navigation__list.active');
    var tabActive = $('#' + tabListActive.attr('aria-controls'));

    if (window.matchMedia('(min-width: 1199px').matches) {
      $('html').removeClass('nav-open');
      $('.main-header-nav-item ').removeClass('expanded');
      $('.main-header-nav-item-secondary').removeClass('expanded');
    }

    if (window.matchMedia('(max-width: 849px)').matches) {
      $('main').addClass('tab-available');
    } else {
      // If nothing was selected on mobile
      if (!tabListActive.length) {
        $('.tab-navigation__container').each(function() {
          $(this)
            .find('.tab-navigation__list')
            .first()
            .addClass('active')
            .attr('aria-expanded', 'true');
        });

        $('.full-bleed__wrapper--tabletMobile-only').each(function(){
          $(this)
            .find('.tab-item')
            .first()
            .find('.tab-contents')
            .show()
            .attr('aria-hidden', 'false');
        });
      }

      $('.tab-navigation__container').each(function() {
        // if one of tab component was not selected.
        if (!$(this).find('.active').length) {
          $(this)
            .find('.tab-navigation__list')
            .first()
            .addClass('active')
            .attr('aria-expanded', 'true');

          $(this)
            .closest('.default-component__wrapper')
            .find('.full-bleed__wrapper--tabletMobile-only')
            .find('.tab-item')
            .first()
            .find('.tab-contents')
            .show()
            .attr('aria-hidden', 'false');
        }
      });

      $('main').removeClass('tab-available');
    }

    if ($('.tab-available').length) {
      // show active tab content
      tableHeader.removeClass('accordion__item--open');
      tabContents.hide().attr('aria-hidden', 'true');

      tabListActive.each(function() {
        $('.tab-header[aria-controls="' + $(this).attr('aria-controls') + '"]')
          .addClass('accordion__item--open')
          .attr('aria-expanded', 'true');

        $('#' + $(this).attr('aria-controls'))
          .find('.tab-contents')
          .show()
          .attr('aria-hidden', 'false');
      });
    }
  });

  $(window).scroll(function() {
    if ($('.tab-header.accordion__item--open').length) {
      var distance = $('.tab-header.accordion__item--open').offset().top;
    }

    if ($(this).scrollTop() >= distance) {
      $('.tab-header.accordion__item--open').addClass('has-shadow');
    } else {
      $('.tab-header.accordion__item--open').removeClass('has-shadow');
    }
  });

  //Language Selector
  var langSelectButton = $('.hk-language__button'),
    langOptions = $('.hk-language__options'),
    langOptionList = $('#language-options li');

  $('.hk-language__button').click(function() {
    langSelectButton.attr('aria-expanded', 'false');
    langOptions.attr('aria-hidden', 'true');

    if (langOptions.css('display') === 'block') {
      $(this)
        .next()
        .hide();
      langOptions.hide();
      langOptions.attr('aria-hidden', 'true');
    } else {
      $(this).attr('aria-expanded', 'true');
      langSelectButton.attr('aria-expanded', 'true');

      $(this)
        .next()
        .show()
        .attr('aria-hidden', 'false');
      langOptions.show().attr('aria-hidden', 'false');
    }
  });

  langOptionList.click(function(e) {
    
    var activeListText = $(this)
      .find('a')
      .text();
    langSelectButton.text(activeListText);
    langOptionList.removeClass('active');
    $(this).addClass('active');

    $(this)
      .closest('.hk-language__options')
      .hide()
      .attr('aria-hidden', 'true');
    langOptions.hide().attr('aria-hidden', 'true');

    $(this)
      .closest('.hk-language')
      .find('.hk-language__button')
      .attr('aria-expanded', 'false');
    langSelectButton.attr('aria-expanded', 'false');

    return true;
  });

  var allPanels = $('.accordion__item-contents');
  var stickyEl = $('.sticky');

  $(window).on('load resize', function() {
    if ($('main').hasClass('tab-available') && stickyEl.length) {
      Stickyfill.add(stickyEl);
    }
  });

  var allPanels = $('.accordion__item-contents');
  var accordionTrigger = $('.js--accordion_trigger'),
    accordionTriggerButton = $('.js--accordion_trigger > button'),
    accrodionItem = $('.accordion__item');

  accordionTrigger.on('click', function(e) {
    accrodionItem.removeClass('accordion__item--open');
    allPanels.slideUp().removeClass('is-opened');
    allPanels.attr('aria-hidden', 'true');
    accordionTriggerButton.attr('aria-expanded', 'false');

    if (
      $(this)
        .next()
        .css('display') === 'none'
    ) {
      $(this)
        .find('button')
        .attr('aria-expanded', 'true');
      var next = $(this).next();
      next.attr('aria-hidden', 'false');
      next
        .slideDown({
          start: function() {
            $(this).css({
              display: 'flex',
            });
          },
        })
        .closest('.accordion__item')
        .addClass('accordion__item--open');
    }
    return false;
  });

  // STICKY FOOTER BUTTON
  $('.sticky-footer__button').click(function() {
    $(this)
      .closest('.sticky-footer')
      .toggleClass('sticky-footer--details');
  });

  // TABBED-TABLE
  $('.tab_content').hide();
  $('.tab_container').each(function() {
    $(this)
      .find('.tab_content')
      .first()
      .show();
  });

  /* if in tab mode */
  $('ul.tabs li').click(function() {
    var tabbedTable = $(this).closest('.tabbed-table');
    tabbedTable.find('.tab_content').hide();
    var activeTab = $(this).attr('rel');
    tabbedTable.find('#' + activeTab).fadeIn();

    tabbedTable.find('ul.tabs li').removeClass('active');
    $(this).addClass('active');

    tabbedTable.find('.tab_drawer_heading').removeClass('d_active');
    tabbedTable
      .find(".tab_drawer_heading[rel^='" + activeTab + "']")
      .addClass('d_active');
  });
  /* if in drawer mode */
  $('.tab_drawer_heading').click(function() {
    var tabbedTable = $(this).closest('.tabbed-table');
    tabbedTable.find('.tab_content').hide();

    var d_activeTab = $(this).attr('rel');
    tabbedTable.find('#' + d_activeTab).fadeIn();

    tabbedTable.find('.tab_drawer_heading').removeClass('d_active');
    $(this).addClass('d_active');

    tabbedTable.find('ul.tabs li').removeClass('active');
    tabbedTable
      .find("ul.tabs li[rel^='" + d_activeTab + "']")
      .addClass('active');
  });
});

function clearInputs() {
  var searchMobile = document.getElementById('search-mobile');
  var searchDesktop = document.getElementById('search');
  var formLogin = document.getElementById('form-login');

  if (searchMobile) {
    searchMobile.reset();
  }
  if (searchDesktop) {
    searchDesktop.reset();
  }
  if (formLogin) {
    formLogin.reset();
  }
}

$(window).on('load', clearInputs);

function focusOnlyTabAnchors() {
  document.body.classList.add('using-mouse');
  document.body.addEventListener('mousedown', function() {
    document.body.classList.add('using-mouse');
  });
  document.body.addEventListener('keydown', function() {
    document.body.classList.remove('using-mouse');
  });
}

$(window).on('load', focusOnlyTabAnchors);

// Alert Banner Cookie Implementation

function createPopupCookie(name, value, exdays) {
  var d = new Date();
  d.setTime(d.getTime() + exdays * 24 * 60 * 60 * 1000);
  var expires = 'expires=' + d.toGMTString();
  var hostName = location.hostname;
  var pathName = location.pathname;
  
  document.cookie = name + "=" + value + ";" + expires + ";path=/";
}

function getCookie(cname) {
  var name = cname + '=';
  var ca = document.cookie.split(';');
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return '';
}

function checkCookie() {
  let alertBanner = document.getElementsByClassName('alert-banner');
  if(alertBanner){
     for (i = 0; i < alertBanner.length; i++) {
    let thisId = alertBanner.item(i).id;
    let thisAlert = document.getElementById(thisId);

    if (getCookie(thisId) == 'clicked') {
      thisAlert.style.display = 'none';
    } else {
      thisAlert.style.display = 'inline';
    }
    }
  }
}

$(window).on('load', checkCookie);

function closeBanner(event) {
  if (
    (event.target.className =
      'close-icon' && event.target.parentElement.parentElement)
  ) {
    let banner = event.target.parentElement.parentElement;
    let bannerId = event.target.id;

    this.createPopupCookie(bannerId, 'clicked', 30);
    banner.style.display = 'none';
  }
}
