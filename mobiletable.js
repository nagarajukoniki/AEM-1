$(document).ready(function() {
  const disableBodyScroll = bodyScrollLock.disableBodyScroll;
  const enableBodyScroll = bodyScrollLock.enableBodyScroll;
   
  let targetElement;
  let rootId;

  $('.js-close-modal').click(function() {
    closeModal(rootId);
    enableBodyScroll(targetElement);
  })

  $('.js-open-modal').click(function() {
    rootId = this.id;
    const modalId = rootId + '-modal';
    event.preventDefault();
    openModal(rootId);
    targetElement = modalId;
    disableBodyScroll(targetElement);
  })
})

function closeModal(id) {
    $('#' + id + '-modal').removeClass('modal-popup').addClass('is-hidden');
    $('#' + id + '-overlay').css({'display': 'none'});
    $('#' + id).focus();
}

function openModal(id) {
    $('#' + id + '-modal').addClass('modal-popup').removeClass('is-hidden');
    $('#' + id + '-overlay').css({'display': 'block'});
    $('#' + id + '-close').focus();
}
