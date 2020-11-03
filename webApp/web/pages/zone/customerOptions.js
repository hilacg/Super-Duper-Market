

function openOrder() {
    const win = document.getElementById("order-container");
    !win.classList.contains("show") && win.classList.add("show");
    $("#order-container div:first-child").css("display","block");
    $("#orderForm").trigger('reset');
    $("#storeProductSelect tbody").empty();
    $("#storeSelect tbody").empty();
    $("#staticOrder-container").css("display", "none");
    $("#dynamicOrder-container").css("display", "none");
    initOrder();
}
function initOrder() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "initOrder",
        },
    })
    $(".cart>span,.cart>br").remove();
    order={
        stores:[]
    };
}


function addToCart(productId,amount,productName) {
    $.ajax({
        url:ORDER_URL,
        data: {
            action: "addToCart",
            zoneName:zoneName,
            owner:ownerId,
            store:order.storeId,
            product:productId,
            amount:amount,
        },
        success: function(response) {
            alert(response);
            $(".cart").append($(`<span>${productName}: </span><span>${amount}</span><br>`))
        },
        error:error=>{
            alert(error.responseText);
        }
    });
}

function chooseAmount(productId,productName) {
    var amount = prompt("Please enter amount to buy:");
    if (!(amount === null || amount === "")) {
        addToCart(productId,amount,productName);
    }
}

function getStoreProducts(storeId) {
    $.ajax({
        url:AREA_URL,
        data: {
            action: "getStoreProducts",
            zoneName:zoneName,
            owner:ownerId,
            store:storeId,
        },
        success: function(json) {
            json.storeProducts.forEach(product => delete product.sold);
            showProducts(json.storeProducts,'#storeProductSelect tbody');
            $("#storeProductSelect tr").click((event)=>chooseAmount(event.target.closest("tr").children[0].innerHTML,event.target.closest("tr").children[1].innerHTML));
        }
    });
}

function selectStore(event) {
    $('#storeSelect tr').removeClass("selected");
    const storeNode = event.target.closest("tr");
    storeNode.classList.add("selected");
    order.storeId =storeNode.children[0].innerHTML;
    getStoreProducts(order.storeId);

}

function staticOrder() {
    initOrder();
    order.type="static";
    $('#staticOrder-container').css("display","block");
    $('#dynamicOrder-container').css("display","none");
    const table = $('#storeSelect tbody');
    table.empty();
    storesJson.forEach(store => {
        var tr = $(document.createElement('tr'));
        tr.append($(document.createElement('td')).text(store["Serial Number"]));
        tr.append($(document.createElement('td')).text(store["Name"]));
        tr.append($(document.createElement('td')).text(store["Location"]));
        tr.appendTo(table);
    })
    $('#storeSelect tr').on("click",selectStore);
}

function dynamicOrder() {
    initOrder();
    order.type="dynamic";
    $('#dynamicOrder-container').css("display","block");
    $('#staticOrder-container').css("display","none");
}

function showOptimalOrder(orderSum) {
    var sum = $(document.createElement('div'));
    sum.addClass("optimalOrder");
    var button = $(document.createElement('button'));
    button.addClass("exit");
    button.addClass("fa fa-lg fa-times");
    button.on("click",()=>{$(".optimalOrder").remove()});
    sum.append(button);
    sum.append($(document.createElement('h1')).text("Optimal Order"));
    orderSum.forEach(storOrder=>{
        var div = $(document.createElement('div'));
        div.addClass("storeSum");
        div.append($(document.createElement('h2')).text(storOrder.storeName));
        for( var storeDetail of Object.keys(storOrder.storeDetails)){
            var divP = $(document.createElement('div'));
            divP.append($(document.createElement('span')).text((storeDetail) + ": "));
            divP.append($(document.createElement('span')).text(storOrder.storeDetails[storeDetail]));
            divP.append($(document.createElement('br')));
            div.append(divP);
        }
        sum.append(div);
    })
    $("#order-container").append(sum);
}

function chargeCustomer() {
    $.ajax({
        url: ACCOUNT_URL,
        data: {
            action: "charge",
            owner: ownerId,
            zoneName: zoneName
        },
        success:(amount)=> {
            alert("You were charged a total of "+ amount+ " Nis");

        }
    })
}

function rateStore(event,storeId) {
    var self = this;
    self.newRating = 0;
    self.containerId = storeId;
    self.newRating = jQuery( event.target ).data( 'stars' );
    $(`.stars.${storeId}`).data("stars",self.newRating);
    $(`.stars.${storeId}`).children().removeClass("checked");

    for(var i= 0; i<self.newRating;i++){
        $(`.stars.${storeId}`).children().eq(i).addClass("checked");
    }
}

function saveReview() {
    var feedbacks=[];

    $(".storeReview.selected").each(function(){
        var feedback={};
        var stars = $(this).find(".stars");
        feedback.storeId = stars.prop("classList")[1];
        feedback.stars = $(stars).data("stars");
        feedback.message = $(this).find("input").val();
        feedbacks.push(feedback);
    })
    $.ajax({
        url: ORDER_URL,
        contentType: 'application/json; charset=utf-8',
        data: {
            action: "feedback",
            feedbacks:JSON.stringify(feedbacks)
        },
        success:(amount)=> {
            alert("Your reviews were saved");

        }
    })
    $(".reviewWindow .exit").click()
}

function reviewWin() {
    var mainDiv = $(document.createElement('div'));
    mainDiv.addClass("reviewWindow")
    var button = $(document.createElement('button'));
    button.addClass("exit");
    button.addClass("fa fa-lg fa-times");
    button.on("click",()=>{$("#order-container>div:gt(0)").remove(); showWindow("order-container")});
    mainDiv.append(button);
    mainDiv.append($(document.createElement('h1')).text("Review Stores"));
    order.stores.forEach(store=>{
        var storeReview =  $(document.createElement('div'));
        storeReview.addClass("storeReview");
        storeReview.addClass(store.id);
        storeReview.click(event=>{
            const store = event.target.closest(".storeReview ");
            store.classList.toggle("selected")});
        storeReview.append($(document.createElement('h2')).text(store.name));
        var starsDiv = $(`<div class="stars ${store.id}" data-stars="1"> 
                                <i class='fa fa-star star checked' data-stars="1"></i>
                                <i class='fa fa-star star' data-stars="2"></i>
                                <i class='fa fa-star star' data-stars="3"></i>
                                <i class='fa fa-star star' data-stars="4"></i>
                                <i class='fa fa-star star' data-stars="5"></i>
                                </div>`);
        starsDiv.click(event=>{
            e = window.event;

            //IE9 & Other Browsers
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            //IE8 and Lower
            else {
                e.cancelBubble = true;
            }
        })
        starsDiv.children().click((event)=>rateStore(event,store.id));
        storeReview.append(starsDiv)
        var text = $('<input type="text" name="feedBack" placeholder="say anything...">')
        text.click(event=>{
            e = window.event;

            //IE9 & Other Browsers
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            //IE8 and Lower
            else {
                e.cancelBubble = true;
            }
        })
        storeReview.append(text);
        mainDiv.append(storeReview);
    })
    var divButtons = $(document.createElement('div'));
    divButtons.addClass("summaryButtons")
    var confirm = $(document.createElement('button')).text("confirm");
    confirm.on("click",()=> {saveReview()});
    divButtons.append(confirm);
    var cancel = $(document.createElement('button')).text("cancel");
    cancel.on("click",()=>$(".reviewWindow .exit").click());
    divButtons.append(cancel);
    mainDiv.append(divButtons);

    showWindow("order-container");
    $("#order-container").append(mainDiv);
}

function confirmOrder() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "confirmOrder",
        },
        success:()=> {
            alert("Order Added Successfully!");
            reviewWin();
            chargeCustomer();
            getProducts();
            getStores();
        }
    })
}

function showOrderSum(json) {
    var orderSum = json.orderSum;
    var sum = $(document.createElement('div'));
    sum.addClass("orderSum");
    var divOrder = $(document.createElement('div'));
    divOrder.addClass("summary-container");
    orderSum.forEach(storeOrder=> {
        var divStore = $(document.createElement('div'));
        var sumList = $(document.createElement('ul'))
        divStore.append(sumList);
        sumList.addClass("storeSum");
        sumList.append($(document.createElement('h2')).text(storeOrder["store Name"]));
        order.stores.push({id:storeOrder.details["store Serial"], name:storeOrder["store Name"]});
        showProductsSum([storeOrder.details], sumList,"p")
        sumList.append($(document.createElement('h3')).text("Product bought:"));
        showProductsSum(storeOrder.product, sumList,"li");
        if (storeOrder.discount.length > 0) {
            sumList.append($(document.createElement('h3')).text("Product from discounts:"));
            showProductsSum(storeOrder.discount, sumList,"li");
        }
        divOrder.append(divStore);
        });
    sum.append(divOrder);
    divOrder = $(document.createElement('div'));
    showProductsSum([json.orderFinalSum],divOrder,"p")
    divOrder.css({
        "width": "fit-content",
    "margin": "auto",
    "font-size": "16px",
    "font-weight": "bold",
    "font-family": "Courier New, Courier, monospace"})
    sum.append(divOrder);
    var divButtons = $(document.createElement('div'));
    divButtons.addClass("summaryButtons")
    var confirm = $(document.createElement('button')).text("confirm");
    confirm.on("click",()=> {$("#orderSummary .exit").click(); confirmOrder()});
    divButtons.append(confirm);
    var cancel = $(document.createElement('button')).text("cancel");
    cancel.on("click",()=>$("#orderSummary .exit").click());
    divButtons.append(cancel);
    sum.append(divButtons);
    return sum;
}

function showProductsSum(products,sumList,type){
    products.forEach(product=>{
        var divProduct = $(document.createElement(type));
        for( var productkey of Object.keys(product)) {
            divProduct.append($(document.createElement('span')).text(productkey + ": " + product[productkey]));
            divProduct.append($(document.createElement('br')));
        }
        divProduct.append($(document.createElement('br')));
        sumList.append(divProduct);
    });
}


function getOrderSum() {
    $.ajax({
        url: ORDER_URL,
        data: {
            action: "getOrderSum",
        },
        success: (json)=>{
            var mainDiv = $(document.createElement('div'));
            mainDiv.attr("id","orderSummary")
            var button = $(document.createElement('button'));
            button.addClass("exit");
            button.addClass("fa fa-lg fa-times");
            button.on("click",()=>{$("#order-container>div:gt(0)").remove(); showWindow("order-container")});
            mainDiv.append(button);
            mainDiv.append($(document.createElement('h1')).text("Order Summary"));
            mainDiv.append(showOrderSum(json));
            $("#order-container").append(mainDiv);
        }
    })
}

function addOrderDiscounts() {
    var requestData = [];
    var selectedDiscount = $(".discount.selected");
    selectedDiscount.each( (index,discount) => {
        var productId = $(discount).find("input:radio:checked").attr('class');
        requestData.push({
            storeId: discount.classList[1],
            discountName: discount.children[0].textContent,
            chosenRadio: $(discount).find("input:radio:checked").next().text(),
            productId: typeof productId !== 'undefined' ? productId : 0
        });
    });
    $.ajax({
        url: ORDER_URL,
        contentType: 'application/json; charset=utf-8',
        data: {
            action: "saveDiscounts",
            discounts:JSON.stringify(requestData),
        },
        success: ()=>getOrderSum()
    })
}

function showDiscounts(discounts) {
    const win =   $(document.createElement('div'));
    win.addClass("discountsWin");
    var button = $(document.createElement('button'));
    button.addClass("exit");
    button.addClass("fa fa-lg fa-times");
    button.on("click",()=>{
        document.getElementById("order-container").classList.toggle("show");
        win.remove();
    });
    win.append(button);
    win.append($(document.createElement('h1')).text("Discounts"));
    discounts.forEach((discount,index)=>{
        var div = $(document.createElement('div'));
        div.addClass("discount");
        div.addClass(discount.storeId.toString());
        div.append($(document.createElement('h2')).text(discount.name));
        div.append($(document.createElement('p')).text("Because you bought: " + discount.quantity+" "+discount.product));
        div.append($(document.createElement('span')).text(" You can get " + discount.operator + ":"));
        var offerDiv =  $(document.createElement('div'));
        offerDiv.css("width","fit-content");
        offerDiv.click(event=>{
            e = window.event;

            //IE9 & Other Browsers
            if (e.stopPropagation) {
                e.stopPropagation();
            }
            //IE8 and Lower
            else {
                e.cancelBubble = true;
            }
        })
        discount.offers.forEach((offer,indx2)=>{
            var of = offer.quantity+" "+ offer.product+ " for additional "+ offer.forAdditional+" Nis";
            switch (discount.operator) {
                case "one of": {
                    var radio = $('<input type="radio" name="' +index+'" id="'+ index + "_" +indx2 +'" value="'+offer.productId+'" checked>');
                    radio.addClass(offer.productId.toString())
                    offerDiv.append(radio);
                    var label = $(document.createElement('label')).text(of);
                    label.prop("for", `${index}_${indx2}`);
                    offerDiv.append(label);

                    break;
                }
                default: {
                    offerDiv.append($(document.createElement('span')).text(of));
                    break;
                }
            }
            offerDiv.append($(document.createElement('br')));
            div.append(offerDiv);
        })
        div.on("click",event=>{
            const discount = event.target.closest(".discount");
            discount.classList.toggle("selected");
        })
        win.append(div);
    })
    button = $(document.createElement('button')).text("Confirm");
    button.on("click",()=>addOrderDiscounts());
    win.append(button);
    $("#order-container").append(win);
}

function finishOrder() {
    $.ajax({
        url: ORDER_URL,
        method: 'GET',
        data: {
            action: "finishOrder",
            type: order.type,
            store: order.storeId,
            x: $("#orderForm .x").val(),
            y: $("#orderForm .y").val(),
            date: document.getElementById("datepicker").value
        },
        success: function (json) {
            $("#order-container div:first-child").css("display","none");
            order.type === "dynamic" && showOptimalOrder(json.orderSum);
            if(json.discounts.length > 0)
                showDiscounts(json.discounts);
            else
                getOrderSum();
        },
        error: (error)=>{
            alert(error.responseText)
        }
    });
}

function showCustomerOrders(json) {

    var div = $(document.createElement('div'))
    div.attr('id', 'order-history');
    div.attr('class', 'popup-window show');
    var mainDiv = $(document.createElement('div'));
    $("body").append(div);
    var button = $(document.createElement('button'));
    button.addClass("exit");
    button.addClass("fa fa-lg fa-times");
    button.on("click", () => {
        $("#order-history").remove();
    });
    mainDiv.append(button);
    mainDiv.append($(document.createElement('h1')).text("Order History"));
    if (json.length === 0)
        mainDiv.append($(document.createElement('span')).text("No orders yet"));
    else {
        json.forEach(order => {
            mainDiv.append(showOrderSum(order));
        })
    }
        div.append(mainDiv);
}

function getCustomerOrders(event) {

    $.ajax({
        url: ORDER_URL,
        data: {
            action: "getCustomerOrders",
        },
        success:(json)=> {
            openOrderHistory();
            if(json.length === 0)
                $('#order-history-container>div').append($(document.createElement('span')).text("No orders yet"));
            else
                showOrders(json,$('#order-history-container>div>div'));
        }
    })
}


$(function() {
    $("#datepicker").datepicker({minDate: 0});
    $("#datepicker").datepicker("option", "dateFormat","dd/mm/yy");

});

