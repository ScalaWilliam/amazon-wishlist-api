var HeaderItemDetail = React.createClass({
    render: function () {
        var {link, title, price, image, addToCartLink, reviewsLink,
            priority, has, wants, comment} = this.props.item;
        return <aside id="item-detail">
            <h2><a href={link} target="_blank">{title}</a></h2>

            <div id="item-detail-flex">
                <div id="item-detail-left">
                    <a href={link} target="_blank">
                        {image && <img src={image.src}
                                       width={image.width}
                                       height={image.height}/>}
                    </a>

                    <p id="item-detail-price">{price}</p>
                </div>
                <div id="item-detail-rest">
                    <ul id="item-detail-actions">
                        <li id="item-detail-action-view-item"><a href={link} target="_blank">View</a></li>
                        <li id="item-detail-action-buy-item"><a href={addToCartLink} target="_blank">Add to basket</a>
                        </li>
                        <li id="item-detail-action-see-reviews"><a href={reviewsLink} target="_blank">See reviews</a>
                        </li>
                    </ul>
                    <p id="item-detail-attributes">
                        Priority: {priority}. I have: {has} and I want: {wants}
                    </p>
                    {comment && <blockquote>{comment}</blockquote>}
                </div>
            </div>
        </aside>;
    }
});
var Header = React.createClass({
    render() {
        var {wishlist, currentItem} = this.props;
        return <header id="fixed-header">
            <h1><a href={wishlist.uri} target="_blank">{wishlist.title} for {wishlist.person}</a></h1>
            {currentItem && <HeaderItemDetail item={currentItem}/>}
        </header>
    }
});
var GridItem = React.createClass({
    event(name) {
        var props = this.props;
        return function () {
            return props[name] && props[name](props.id);
        }
    },
    render() {
        var {image, selected} = this.props;
        return <li
            onMouseOver={this.event('onhover')}
            onMouseOut={this.event('onunhover')}
            onClick={this.event('onselect')}
            className={{selected: selected}}
            >
            {image && <img src={image.src} width={image.width} height={image.height}/>}
        </li>
    }
});
var Grid = React.createClass({
    event(name) {
        var props = this.props;
        return function (id) {
            return props[name] && props[name](id);
        }
    },
    render() {
        var {items} = this.props.wishlist;
        var {selected} = this.props;
        var props = this.props;
        return <ul id="main-list">
            {items.map((item) => {
                return <GridItem
                    selected={item.id == selected}
                    key={item.id} {... item}
                    onunhover={this.event('onunhover')}
                    onhover={this.event('onhover')}
                    onselect={this.event('onselect')}
                    />;
            })}
        </ul>;
    }
});
var MMain = React.createClass({
    getInitialState() {
        return {
            hovered: null,
            selected: null,
            wishlist: {items: []}
        }
    },
    componentWillMount() {
        $.get('get').then((resp) => {
            this.setState({wishlist: resp});
        });
    },
    onSelect(id) {
        this.setState({selected: id});
    },
    onHover(id) {
        this.setState({hovered: id});
    },
    onUnhover() {
        return this.setState({hovered: null});
    },
    currentItem() {
        var itemId = this.state.hovered || this.state.selected;
        if (!itemId) return;
        return this.state.wishlist.items.filter((item) => item.id == itemId).shift();
    },
    render() {
        return <div>
            <Header
                wishlist={this.state.wishlist}
                currentItem={this.currentItem()}
                />
            <Grid
                selected={this.state.hovered || this.state.selected }
                wishlist={this.state.wishlist}
                onselect={this.onSelect}
                onhover={this.onHover}
                onunhover={this.onUnhover}
                />
        </div>
    }
});

React.render(
    <MMain/>,
    document.querySelector('#main-stuff')
);
