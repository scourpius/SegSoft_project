package src.AccessController;

enum Operation {
    access_page,
    create_page,
    delete_page,
    submit_follow,
    authorize_follow,
    access_post,
    create_post,
    delete_post,
    like_post
}


/**
 * Notes:
 * Normal users can:
 *      access all pages in the system.
 *      submit follow requests to all pages in the system.
 *      authorize follow requests to their pages from authors in the system (by upgrading status from 1 to 2).
 *      only access posts on pages they follow.
 *      only create posts on their own page.
 *      only delete posts on their own page.
 *      like / unlike all posts in pages they follow.

 * Admin authors have all the same authorizations as normal users
 * As default, SN newPage and deletePage should only be usable by admin authors
 */