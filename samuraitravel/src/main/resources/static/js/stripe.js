const stripe = Stripe('pk_test_51RstPnLIqMKO2K6QiHaYqSJTPmEDj1NBygvviIGS7Jo1XjFHxU3InHBDtuLXkbCvfvXfVWZPVtEbX9vzkOeGR3iX00rfZQJHkM');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
 stripe.redirectToCheckout({
   sessionId: sessionId
 })
});