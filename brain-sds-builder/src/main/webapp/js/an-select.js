// 검색 UI가 포함된 셀렉트 리스트
// --html
// <input type="text" id="select_radio" class="select" readOnly> //radio list
// <input type="text" id="select_checkbox" class="select" multiple readOnly> //checkbox list
//
// --js (@popperjs/core v2.5.4 - MIT License 연결필요)
// var selectRadio = createSearchSelect($('#select_radio')); //radio element 생성
// var selectCheckbox = createSearchSelect($('#select_checkbox')); //checkbox element 생성
//
// selectRadio.setOptions([ //option 세팅
//     {'label': 'test1', 'value': 'test1'},
//     {'label': 'test2', 'value': 'test2'},
//     {'label': 'test3', 'value': 'test3'},
// ]);
// selectRadio.getOptions(); //세팅된 option 값 가져오기
// selectRadio.setValue([ //value 세팅
//     'test1'
// ]);
// selectCheckbox.setValue([ //value 세팅 (html multiple 이면 여러 value 입력 가능)
//     'test1',
//     'test2',
// ]);
// selectRadio.getValue(); //세팅된 value 값 가져오기
// selectRadio.clearValue(); //세팅된 value 초기화
// selectRadio.clearOptions(); //setOptions 초기화
// selectRadio.open(); //리스트 열기
// selectRadio.close(); //리스트 닫기
// selectRadio.remove(); //element 삭제
//
// selectRadio.root //.an-search_select
// selectRadio.select //.select
// selectRadio.searchInput //.an-input_text.search
// selectRadio.options //.an-search_option

function createSearchSelect($select){
    var id = $select.attr('id');
    var isMultiple = $select.attr('multiple') ? 'checkbox' : 'radio';
    var $rootElement = $('<div id="'+ 'root_' + id +'" class="an-search_select"></div>');
    var $optionElement = $('<div class="an-search_option"></div>');
    var $searchWrapper = $('<div class="an-search_box"></div>');
    var $searchElement = $('<input type="text" class="an-input_text search">');
    var $optionList = $('<ul class="an-select_list scroll"></ul>');
    var $option = null;
    var $noOption = $('<li><p>등록된 리스트가 없습니다.</p></li>');
    var options = [];
    var selectedValues = []; //선택된 values

    $select.attr('role', 'select');
    $select.replaceWith($rootElement);
    $rootElement.append($select);
    $searchWrapper.append($searchElement);
    $optionElement.append($searchWrapper);
    $rootElement.append($optionElement);
    $optionList.append($noOption);

    var optionListController = Popper.createPopper($select[0], $optionElement[0], {
        strategy: 'fixed',
        modifiers: [
            {
                name: 'offset',
                options: {
                    offset: [0, 5],
                },
            },
        ],
        flipVariations: true,
    });

    setTimeout(function(){
        $optionElement.appendTo("body");
    });

    $searchElement.on('keyup', function(){
        var lists = options;
        var inputVal = $searchElement.val();
        var listLength = 0;

        $optionElement.find($noOption).remove();

        for (var i = 0, l = lists.length; i < l; i++) {
            var label = lists[i].label;
            if (label.indexOf(inputVal) > -1) {
                $optionElement.find('li').eq(i).show();
                listLength += 1;
            } else {
                $optionElement.find('li').eq(i).hide();
            }
        }

        if (!listLength) {
            $optionElement.find('ul').append($noOption);
        }
    });

    $select.on('click', function(e){ //리스트 열기 닫기
        e.stopPropagation();
        if ( $optionElement.hasClass('active') ) {
            $('body').find('.an-search_option').removeClass('active');
            return;
        }

        $('body').find('.an-search_option').removeClass('active');
        $optionElement.addClass('active');

        $optionElement.width($rootElement.width());
        optionListController.forceUpdate();
    });

    $optionElement.on('click', function(e){
        e.stopPropagation(); //body 버블링막기
    });

    $('body').on('click', handleBodyClick);
    function handleBodyClick (){
        $optionElement.removeClass('active'); // 리스트 닫기
    }

    $select.on('change', function(){
        var selectLabel = function(){
            if ( selectedValues.length === 1 ){
                return selectedValues[0].label;
            } else if ( selectedValues.length > 1 ) {
                var labels = '';
                for (var i=0, l=selectedValues.length; i<l; i++){
                    if ( i === l-1 ){
                        labels += selectedValues[i].label;
                    } else {
                        labels += selectedValues[i].label + ', ';
                    }
                }
                return labels;
            }
        }
        $(this).val(selectLabel); //선택된 label 업데이트
        getValue(); //선택된 value 업데이트
    });

    function getOptions(){
        return options;
    }

    function setOptions(datum){
        var datumLeng = !datum ? 0 : datum.length;
        options = datum;
        $optionList.empty();

        if ( !datumLeng ){
            $optionList.append($noOption);
        } else {
            for (var i = 0, l = datum.length; i < l; i++){
                var list = datum[i];
                var label = list.label;
                var value = list.value;
                $option = $('<li>\
              <input type="'+ isMultiple +'"'+
                    ' name="'+ id +'"' +
                    ' id="' + value + '"' +
                    ' class="' + isMultiple + '"' +
                    ' data-label="' + label +
                    '">\
                <label for="' + value + '">' + label + '</label>\
            </li>');

                $option.on('change', 'input', function(){
                    var optionValue = $(this).attr('id');
                    var optionLabel = $(this).attr('data-label');
                    var obj = {'label': optionLabel, 'value': optionValue};

                    if ( isMultiple === 'radio' ){
                        selectedValues = [];
                        selectedValues.push(obj);
                    } else {
                        if ( $(this).is(':checked') ){
                            selectedValues.push(obj);
                        } else {
                            var index = selectedValues.findIndex(idx => idx.value ===  optionValue );
                            selectedValues.splice(index, 1);
                        }
                    }
                    $select.trigger('change');
                });
                $optionList.append($option);
            }
        }
        $optionElement.append($optionList);
        checkValue();

        function checkValue(){ //선택된 value 체크
            var length = selectedValues.length;
            var selected = '';

            if ( length === 1 ){
                selected = selectedValues[0].value;
                $optionList.find('#' + selected ).prop('checked', true);
            } else if ( length > 1 ) {
                for (var ci = 0, cl = length; ci < cl; ci++){
                    selected = selectedValues[ci].value;
                    $optionList.find('#' + selected ).prop('checked', true);
                }
            }
        }
    }

    function getValue(){
        return selectedValues;
    }

    function setValue(datum){
        var datumLeng = !datum ? 0 : datum.length;
        var datumValue = '';
        selectedValues = [];

        $optionList.find('input').prop('checked', false);

        if ( datumLeng === 1 ) setData(0);
        else if ( datumLeng > 1 ) {
            if ( isMultiple === 'radio' ){
                console.log('[setValue] Check the role of input. The role of this input is radio.');
                return;
            }

            for (var ci=0, cl=datum.length; ci<cl; ci++) {
                setData(ci);
            }
        }
        $select.trigger('change');

        function setData(idx) {
            datumValue = datum[idx];
            var option = '#' + datumValue;
            var datumLabel = $optionList.find(option).attr('data-label');
            var obj = {'label': datumLabel, 'value': datumValue};

            $optionList.find(option).prop('checked', true);
            selectedValues.push(obj);
        }
    }

    function clearOptions(){ //setOptions 초기화
        if ( !$option ) return;
        $option.off('change');
        setOptions();
    }

    function open(){
        $optionElement.addClass('active');
    }

    function close(){
        $optionElement.removeClass('active');
    }

    function remove(){ //모든 이벤트 제거 후 전체 remove
        $select.off('click');
        $optionElement.off('click');
        $('body').off('click', handleBodyClick);
        $select.off('change');
        // $option.off('change');
        $rootElement.remove();
        $optionElement.remove();
    }

    function show(){
        $rootElement.show();
    }

    function hide(){
        $rootElement.hide();
    }

    return {
        getOptions: getOptions,
        setOptions: setOptions,
        getValue: getValue,
        setValue: setValue,
        clearOptions: clearOptions,
        clearValue: setValue,
        open: open,
        close: close,
        remove: remove,
        show: show,
        hide: hide,
        root: $rootElement,
        select: $select,
        searchInput: $searchElement,
        options: $optionElement,
        search: $searchWrapper,
    }
}